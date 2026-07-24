package com.wingedsheep.gameserver.replay

import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.gameserver.ScenarioTestBase
import com.wingedsheep.gameserver.persistence.toPersistent
import com.wingedsheep.gameserver.session.GameSession
import com.wingedsheep.gameserver.session.PlayerSession
import com.wingedsheep.gameserver.session.SpectatorSeat
import com.wingedsheep.gameserver.session.SpectatorStateBuilder
import com.wingedsheep.sdk.model.EntityId
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.web.socket.WebSocketSession
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

/**
 * Proves the compact replay format is lossless: a game driven through the real [GameSession]
 * recording path, rebuilt from nothing but its captured setup + action stream, re-simulates to a
 * byte-identical final [com.wingedsheep.engine.state.GameState] and produces one viewer frame per
 * input. This is the load-bearing guarantee behind storing inputs instead of snapshots — if entity
 * ids, the RNG, or any read weren't deterministic, the reconstructed state would diverge.
 */
class CompactReplayReconstructionTest : ScenarioTestBase() {

    private fun mockWs(id: String): WebSocketSession =
        mockk(relaxed = true) { every { this@mockk.id } returns id }

    init {
        test("a recorded game reconstructs to a byte-identical final state and full frame stream") {
            val session = GameSession(cardRegistry = cardRegistry, maxPlayers = 2)
            val p1 = EntityId.of("player-1")
            val p2 = EntityId.of("player-2")
            session.addPlayer(PlayerSession(mockWs("ws1"), p1, "Alice"), mapOf("Forest" to 40))
            session.addPlayer(PlayerSession(mockWs("ws2"), p2, "Bob"), mapOf("Forest" to 40))
            session.startGame()

            // Leave the mulligan phase (first keep bottoms nothing).
            session.keepHand(p1)
            session.keepHand(p2)

            // Drive several turns purely by passing priority. Draws at each turn are seed-driven, so
            // this still exercises RNG threading and per-turn entity creation — exactly what would
            // diverge if reconstruction weren't deterministic.
            repeat(60) {
                val state = session.getStateForTesting() ?: return@repeat
                if (state.gameOver) return@repeat
                val priority = state.priorityPlayerId ?: return@repeat
                session.executeAutoPass(priority)
            }

            val setup = session.getReplaySetup().shouldNotBeNull()
            val actions = session.getRecordedActions()
            actions.shouldNotBeEmpty()
            val liveFinal = session.getStateForTesting().shouldNotBeNull()

            val replay = CompactReplay(
                gameId = session.sessionId,
                players = session.getPlayers().map { ReplayPlayerInfo(it.playerId.value, it.playerName) },
                startedAt = Instant.now().toString(),
                endedAt = Instant.now().toString(),
                winnerName = null,
                setup = setup,
                actions = actions,
            )

            // The durable gzip+base64 codec round-trips the exact record.
            ReplayCodec.decode(ReplayCodec.encode(replay)) shouldBe replay

            val reconstructor = ReplayReconstructor(cardRegistry, null)

            val reconFinal = reconstructor.reconstructStateAt(replay, actions.size).shouldNotBeNull()

            // The deterministic core of the game re-simulates byte-for-byte: every entity, every
            // zone, the RNG, the entity-id counter, and the stack. (Only `pendingDecision` /
            // `continuationStack` can differ, and solely in their UUID routing ids, which the engine
            // mints non-deterministically and which never reach the player-visible projection.)
            reconFinal.entities shouldBe liveFinal.entities
            reconFinal.zones shouldBe liveFinal.zones
            reconFinal.rng shouldBe liveFinal.rng
            reconFinal.nextEntityId shouldBe liveFinal.nextEntityId
            reconFinal.stack shouldBe liveFinal.stack
            reconFinal.turnNumber shouldBe liveFinal.turnNumber

            // The player-visible spectator projection (what the replay viewer actually shows) is
            // identical — it carries no decision id, so it matches exactly.
            val builder = SpectatorStateBuilder(cardRegistry, ClientStateTransformer(cardRegistry))
            val seats = setup.players.map { SpectatorSeat(EntityId.of(it.playerId), it.name) }
            builder.buildState(reconFinal, seats, setup.seatRoster, session.sessionId) shouldBe
                builder.buildState(liveFinal, seats, setup.seatRoster, session.sessionId)

            // The viewer stream is initial-frame + one-per-action, with no mid-replay divergence
            // (a divergence would truncate the delta list short).
            val reconstructed = reconstructor.reconstruct(replay)
            reconstructed.frameCount shouldBe (1 + actions.size)

            // Durable profile replays materialize that known-good viewer stream while the finishing
            // game's engine/card definitions are still loaded. A later deployment must use it
            // directly rather than attempting to re-simulate the old action stream.
            var durableReplay: CompactReplay? = null
            val store = object : ReplayStore {
                override fun save(replay: CompactReplay) {
                    durableReplay = replay
                }

                override fun findByGameId(gameId: String): CompactReplay? = durableReplay
            }
            val service = ReplayService(GameHistoryRepository(), store, reconstructor)
            service.save(replay, durable = true)
            val stored = durableReplay.shouldNotBeNull()
            stored.viewerStream shouldBe reconstructed
            ReplayCodec.decode(ReplayCodec.encode(stored)) shouldBe stored

            val newerIncompatibleEngine = mockk<ReplayReconstructor>()
            every { newerIncompatibleEngine.reconstruct(any()) } throws
                AssertionError("stored viewer stream should bypass reconstruction")
            val afterDeployment = ReplayService(GameHistoryRepository(), store, newerIncompatibleEngine)
            afterDeployment.reconstruct(stored) shouldBe reconstructed
        }

        test("a game whose state was injected (no recorded setup) is not replayable") {
            val session = GameSession(cardRegistry = cardRegistry, maxPlayers = 2)
            // No startGame()/addPlayer flow: simulate a dev-scenario injection.
            session.getReplaySetup() shouldBe null
            session.getReplayFrameCount() shouldBe 0
        }

        test("Redis snapshots keep game state and replay actions on the same action boundary") {
            val session = GameSession(cardRegistry = cardRegistry, maxPlayers = 2)
            val p1 = EntityId.of("snapshot-player-1")
            val p2 = EntityId.of("snapshot-player-2")
            session.addPlayer(PlayerSession(mockWs("snapshot-ws1"), p1, "Alice"), mapOf("Forest" to 40))
            session.addPlayer(PlayerSession(mockWs("snapshot-ws2"), p2, "Bob"), mapOf("Forest" to 40))
            session.startGame()
            session.keepHand(p1)
            session.keepHand(p2)

            val running = AtomicBoolean(true)
            val failures = ConcurrentLinkedQueue<String>()
            val reconstructor = ReplayReconstructor(cardRegistry, null)
            val writer = thread(name = "replay-snapshot-writer") {
                try {
                    repeat(200) {
                        val state = session.getStateForTesting() ?: return@repeat
                        if (state.gameOver) return@repeat
                        val priority = state.priorityPlayerId ?: return@repeat
                        session.executeAutoPass(priority)
                    }
                } finally {
                    running.set(false)
                }
            }

            do {
                val persisted = session.toPersistent(lobbyId = null)
                val setup = persisted.replaySetup ?: continue
                val replay = CompactReplay(
                    gameId = persisted.sessionId,
                    players = setup.players.map { ReplayPlayerInfo(it.playerId, it.name) },
                    startedAt = persisted.replayStartedAt ?: Instant.EPOCH.toString(),
                    endedAt = Instant.EPOCH.toString(),
                    winnerName = null,
                    setup = setup,
                    actions = persisted.recordedActions,
                    yields = persisted.recordedYields,
                )
                val reconstructed = reconstructor.reconstructStateAt(replay, replay.actions.size)
                val persistedState = persisted.gameState
                if (reconstructed == null || persistedState == null ||
                    reconstructed.entities != persistedState.entities ||
                    reconstructed.zones != persistedState.zones ||
                    reconstructed.turnNumber != persistedState.turnNumber
                ) {
                    failures.add(
                        "state/action mismatch at ${persisted.recordedActions.size} recorded actions"
                    )
                }
            } while (running.get())

            writer.join()
            failures.toList() shouldBe emptyList()
        }
    }
}
