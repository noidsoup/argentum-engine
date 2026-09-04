package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Elderfang Venom (KHC #15).
 *
 * Attacking Elves you control have deathtouch.
 * Whenever an Elf you control dies, each opponent loses 1 life and you gain 1 life.
 */
class ElderfangVenomScenarioTest : ScenarioTestBase() {

    init {
        context("Elderfang Venom") {

            test("grants deathtouch only while an Elf you control is attacking") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Elderfang Venom")
                    .withCardOnBattlefield(1, "Elvish Warrior", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elf = game.findPermanent("Elvish Warrior")!!

                withClue("not attacking yet, so no deathtouch") {
                    game.state.projectedState.hasKeyword(elf, Keyword.DEATHTOUCH) shouldBe false
                }

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Elvish Warrior" to 2)).error shouldBe null

                withClue("attacking Elf has deathtouch") {
                    game.state.projectedState.hasKeyword(elf, Keyword.DEATHTOUCH) shouldBe true
                }
            }

            test("when an Elf you control dies, each opponent loses 1 life and you gain 1") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Elderfang Venom")
                    .withCardOnBattlefield(1, "Elvish Warrior")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(1, "Murder")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elf = game.findPermanent("Elvish Warrior")!!
                val oppLifeBefore = game.getLifeTotal(2)
                val lifeBefore = game.getLifeTotal(1)

                game.castSpell(1, "Murder", elf).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("opponent loses 1") { game.getLifeTotal(2) shouldBe (oppLifeBefore - 1) }
                withClue("controller gains 1") { game.getLifeTotal(1) shouldBe (lifeBefore + 1) }
            }
        }
    }
}
