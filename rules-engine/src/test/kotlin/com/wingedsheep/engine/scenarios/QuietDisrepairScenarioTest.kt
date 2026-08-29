package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Quiet Disrepair (FUT #134 / PC2 #75) — upkeep modal destroy enchanted permanent or gain 2 life.
 */
class QuietDisrepairScenarioTest : ScenarioTestBase() {

    private val destroyMode = "Destroy enchanted permanent."
    private val lifeMode = "You gain 2 life."

    private fun TestGame.chooseMode(decision: ChooseOptionDecision, description: String) {
        val index = decision.options.indexOf(description)
        check(index >= 0) { "Mode '$description' not offered; options=${decision.options}" }
        submitDecision(OptionChosenResponse(decision.id, index))
    }

    init {
        context("Quiet Disrepair") {
            test("upkeep destroy mode destroys the enchanted artifact and the Aura") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Enchantment")
                    .withCardAttachedTo(1, "Quiet Disrepair", "Test Enchantment")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                val choice = game.getPendingDecision().shouldBeInstanceOf<ChooseOptionDecision>()
                game.chooseMode(choice, destroyMode)
                game.resolveStack()

                withClue("Test Enchantment was destroyed") {
                    game.isOnBattlefield("Test Enchantment") shouldBe false
                }
                withClue("Quiet Disrepair left the battlefield with its host") {
                    game.isOnBattlefield("Quiet Disrepair") shouldBe false
                }
            }

            test("upkeep life mode gains 2 life") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Test Enchantment")
                    .withCardAttachedTo(1, "Quiet Disrepair", "Test Enchantment")
                    .withLifeTotal(1, 18)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                val choice = game.getPendingDecision().shouldBeInstanceOf<ChooseOptionDecision>()
                game.chooseMode(choice, lifeMode)
                game.resolveStack()

                withClue("controller gains 2 life") {
                    game.getLifeTotal(1) shouldBe 20
                }
                withClue("Test Enchantment remains enchanted") {
                    game.isOnBattlefield("Test Enchantment") shouldBe true
                    game.isOnBattlefield("Quiet Disrepair") shouldBe true
                }
            }
        }
    }
}
