package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.arb.cards.KathariRemnant
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Kathari Remnant (ARB #23) — {2}{U}{B} Creature — Bird Skeleton 0/1 with flying and cascade.
 */
class KathariRemnantScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        cardRegistry.register(KathariRemnant)

        context("Kathari Remnant") {

            test("casting resolves a 0/1 flyer onto the battlefield") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Kathari Remnant")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Island") }
                val game = builder.build()

                game.castSpell(1, "Kathari Remnant").error shouldBe null

                var guard = 0
                while (game.getPendingDecision() != null && guard++ < 12) {
                    when (game.getPendingDecision()) {
                        is YesNoDecision -> game.answerYesNo(false) // decline cascade free cast if offered
                        else -> game.submitManaSourcesAutoPay()
                    }
                    game.resolveStack()
                }
                game.resolveStack()

                val remnant = game.findPermanent("Kathari Remnant")!!
                withClue("Kathari Remnant is on the battlefield") {
                    game.isOnBattlefield("Kathari Remnant") shouldBe true
                }
                withClue("Kathari Remnant is a 0/1") {
                    projector.getProjectedPower(game.state, remnant) shouldBe 0
                    projector.getProjectedToughness(game.state, remnant) shouldBe 1
                }
                withClue("Kathari Remnant has flying") {
                    game.state.projectedState.hasKeyword(remnant, Keyword.FLYING) shouldBe true
                }
            }
        }
    }
}
