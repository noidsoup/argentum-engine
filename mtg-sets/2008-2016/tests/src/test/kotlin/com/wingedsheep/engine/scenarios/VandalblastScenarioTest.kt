package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Vandalblast (RTR #111) — {R} sorcery with overload {4}{R}: destroy one artifact you don't control,
 * or overload to destroy each artifact you don't control.
 */
class VandalblastScenarioTest : ScenarioTestBase() {

    init {
        context("Vandalblast") {
            test("printed cast destroys only the chosen artifact you don't control") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Vandalblast")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(1, "Sol Ring")
                    .withCardOnBattlefield(2, "Sol Ring")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val opponentRing = game.findPermanents("Sol Ring").single { entityId ->
                    game.state.projectedState.getController(entityId) == game.player2Id
                }
                val ownRing = game.findPermanents("Sol Ring").single { entityId ->
                    game.state.projectedState.getController(entityId) == game.player1Id
                }

                val cast = game.castSpell(1, "Vandalblast", targetId = opponentRing)
                withClue("Printed cast should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Opponent's Sol Ring is destroyed") {
                    game.state.getBattlefield().contains(opponentRing) shouldBe false
                }
                withClue("Caster's Sol Ring survives the single-target mode") {
                    game.state.getBattlefield().contains(ownRing) shouldBe true
                }
            }

            test("overload destroys each artifact the caster does not control") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Vandalblast")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardOnBattlefield(1, "Sol Ring")
                    .withCardOnBattlefield(2, "Sol Ring")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpellWithOverload(1, "Vandalblast")
                withClue("Overload cast should succeed without targets: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val ownRing = game.findPermanents("Sol Ring").single { entityId ->
                    game.state.projectedState.getController(entityId) == game.player1Id
                }
                withClue("Opponent's Sol Ring is destroyed by the overload sweep") {
                    game.findPermanents("Sol Ring").count { entityId ->
                        game.state.projectedState.getController(entityId) == game.player2Id
                    } shouldBe 0
                }
                withClue("Caster's Sol Ring survives the overload sweep") {
                    game.state.getBattlefield().contains(ownRing) shouldBe true
                }
            }
        }
    }
}
