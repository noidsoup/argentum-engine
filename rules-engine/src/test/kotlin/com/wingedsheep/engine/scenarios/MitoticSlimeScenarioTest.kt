package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.m11.cards.MitoticSlime
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Mitotic Slime (M11 #185) — {4}{G} Creature — Ooze 4/4.
 *
 * When this creature dies, create two 2/2 green Ooze creature tokens.
 */
class MitoticSlimeScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        cardRegistry.register(MitoticSlime)

        context("Mitotic Slime") {

            fun greenOozeTokens(game: TestGame, playerId: EntityId = game.player1Id): List<EntityId> =
                game.state.getZone(playerId, Zone.BATTLEFIELD).filter { id ->
                    game.state.getEntity(id)?.get<TokenComponent>() != null &&
                        game.state.projectedState.getSubtypes(id)
                            .any { it.equals("Ooze", ignoreCase = true) }
                }

            test("dying creates two 2/2 green Ooze tokens") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Mitotic Slime")
                    .withCardInHand(1, "Murder")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val slime = game.findPermanent("Mitotic Slime")!!

                game.castSpell(1, "Murder", slime).error shouldBe null
                game.resolveStack() // Murder + death
                game.resolveStack() // dies trigger

                withClue("Mitotic Slime left the battlefield") {
                    game.isOnBattlefield("Mitotic Slime") shouldBe false
                }

                val tokens = greenOozeTokens(game)
                withClue("two green Ooze tokens are created") {
                    tokens.size shouldBe 2
                }
                tokens.forEach { tokenId ->
                    withClue("each token is a 2/2 green Ooze") {
                        projector.getProjectedPower(game.state, tokenId) shouldBe 2
                        projector.getProjectedToughness(game.state, tokenId) shouldBe 2
                    }
                }
            }
        }
    }
}
