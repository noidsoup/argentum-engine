package com.wingedsheep.gameserver.persistence

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.view.ClientEvent
import com.wingedsheep.gameserver.persistence.dto.PersistentGameSession
import com.wingedsheep.gameserver.persistence.dto.PersistentPlayerInfo
import com.wingedsheep.gameserver.session.GameSession
import com.wingedsheep.gameserver.session.PlayerIdentity
import com.wingedsheep.gameserver.session.SessionRegistry
import com.wingedsheep.sdk.model.EntityId

/**
 * Converts a GameSession to its persistent representation.
 */
fun GameSession.toPersistent(
    lobbyId: String?
): PersistentGameSession {
    val snapshot = getPersistenceSnapshot()
    return PersistentGameSession(
        sessionId = sessionId,
        gameState = snapshot.gameState,
        deckLists = snapshot.deckLists.mapKeys { it.key.value },
        sideboards = snapshot.sideboards.mapKeys { it.key.value },
        lastProcessedMessageId = snapshot.lastProcessedMessageIds.mapKeys { it.key.value },
        gameLogs = snapshot.gameLogs.mapKeys { it.key.value },
        playerInfos = snapshot.playerInfos.map { (playerId, info) ->
            PersistentPlayerInfo(
                playerId = playerId.value,
                playerName = info.playerName,
                token = info.token,
                isAi = info.isAi,
                aiModelOverride = info.aiModelOverride
            )
        },
        lobbyId = lobbyId,
        replaySetup = snapshot.replaySetup,
        recordedActions = snapshot.recordedActions,
        recordedYields = snapshot.recordedYields,
        replayStartedAt = snapshot.replayStartedAt?.toString(),
    )
}

/**
 * Restores a GameSession from its persistent representation.
 *
 * This creates a new GameSession with the persisted state restored.
 * Player sessions are NOT restored here - they are recreated when players reconnect.
 *
 * @param persistent The persisted session data
 * @param cardRegistry The card registry for rebuilding the session
 * @return A new GameSession with state restored, and a list of player identities to register
 */
private val logger = org.slf4j.LoggerFactory.getLogger("GameSessionConverter")

fun restoreGameSession(
    persistent: PersistentGameSession,
    cardRegistry: CardRegistry,
    printingRegistry: com.wingedsheep.engine.registry.PrintingRegistry? = null,
): Pair<GameSession, List<PlayerIdentity>> {
    val session = GameSession(
        sessionId = persistent.sessionId,
        cardRegistry = cardRegistry,
        printingRegistry = printingRegistry,
    )

    logger.info("Restoring game ${persistent.sessionId}: gameState=${if (persistent.gameState != null) "present" else "NULL"}, players=${persistent.playerInfos.size}")

    // Convert persisted data back to EntityId-keyed maps
    val deckLists = persistent.deckLists.mapKeys { EntityId(it.key) }
    val sideboards = persistent.sideboards.mapKeys { EntityId(it.key) }
    val lastMessageIds = persistent.lastProcessedMessageId.mapKeys { EntityId(it.key) }
    val logs = persistent.gameLogs.mapKeys { EntityId(it.key) }
        .mapValues { it.value.toMutableList() }

    // Restore internal state
    session.restoreFromPersistence(
        state = persistent.gameState,
        decks = deckLists,
        logs = logs,
        lastIds = lastMessageIds,
        sideboardLists = sideboards
    )

    // Restore the compact-replay recording so a game interrupted by a restart can still be saved.
    session.restoreReplayRecording(
        setup = persistent.replaySetup,
        actions = persistent.recordedActions,
        startedAtIso = persistent.replayStartedAt,
        yields = persistent.recordedYields,
    )

    // Restore player persistence info
    val playerInfo = persistent.playerInfos.associate { info ->
        EntityId(info.playerId) to GameSession.PlayerPersistenceInfo(
            playerName = info.playerName,
            token = info.token,
            isAi = info.isAi,
            aiModelOverride = info.aiModelOverride
        )
    }
    session.restorePlayerPersistenceInfo(playerInfo)

    // Create PlayerIdentity objects for each persisted player
    val playerIdentities = persistent.playerInfos.map { info ->
        PlayerIdentity(
            token = info.token,
            playerId = EntityId(info.playerId),
            playerName = info.playerName,
            isAi = info.isAi,
            aiModelOverride = info.aiModelOverride
        ).also {
            it.currentGameSessionId = persistent.sessionId
            it.currentLobbyId = persistent.lobbyId
        }
    }

    return session to playerIdentities
}
