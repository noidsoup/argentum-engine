package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.arb.cards.EnlistedWurm
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Enlisted Wurm (ARB #68) — {4}{G}{W} Creature — Wurm 5/5 with cascade.
 */
class EnlistedWurmScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        cardRegistry.register(EnlistedWurm)

        context("Enlisted Wurm") {

            test("casting resolves a 5/5 Wurm onto the battlefield") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Enlisted Wurm")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withLandsOnBattlefield(1, "Plains", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                val game = builder.build()

                game.castSpell(1, "Enlisted Wurm").error shouldBe null

                var guard = 0
                while (game.getPendingDecision() != null && guard++ < 12) {
                    when (game.getPendingDecision()) {
                        is YesNoDecision -> game.answerYesNo(false) // decline cascade free cast if offered
                        else -> game.submitManaSourcesAutoPay()
                    }
                    game.resolveStack()
                }
                game.resolveStack()

                val wurm = game.findPermanent("Enlisted Wurm")!!
                withClue("Enlisted Wurm is on the battlefield") {
                    game.isOnBattlefield("Enlisted Wurm") shouldBe true
                }
                withClue("Enlisted Wurm is a 5/5") {
                    projector.getProjectedPower(game.state, wurm) shouldBe 5
                    projector.getProjectedToughness(game.state, wurm) shouldBe 5
                }
            }
        }
    }
}
