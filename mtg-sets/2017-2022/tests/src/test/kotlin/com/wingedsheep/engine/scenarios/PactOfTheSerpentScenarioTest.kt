package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Pact of the Serpent (KHC #9).
 *
 * Choose a creature type. Target player draws X cards and loses X life, where X is the number of
 * creatures they control of the chosen type.
 */
class PactOfTheSerpentScenarioTest : ScenarioTestBase() {

    init {
        context("Pact of the Serpent") {

            test("target player draws and loses life equal to creatures of the chosen type they control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Pact of the Serpent")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardOnBattlefield(2, "Elvish Warrior")
                    .withCardOnBattlefield(2, "Elvish Warrior")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInLibrary(2, "Plains")
                    .withCardInLibrary(2, "Plains")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val oppLifeBefore = game.getLifeTotal(2)
                val handBefore = game.handSize(2)

                game.castSpellTargetingPlayer(1, "Pact of the Serpent", 2).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                val typeDecision = game.getPendingDecision().shouldBeInstanceOf<ChooseOptionDecision>()
                val elfIndex = typeDecision.options.indexOf("Elf")
                game.submitDecision(OptionChosenResponse(typeDecision.id, elfIndex))
                game.resolveStack()

                withClue("two Elves on battlefield → draw two, lose two life") {
                    game.handSize(2) shouldBe (handBefore + 2)
                    game.getLifeTotal(2) shouldBe (oppLifeBefore - 2)
                }
            }
        }
    }
}
