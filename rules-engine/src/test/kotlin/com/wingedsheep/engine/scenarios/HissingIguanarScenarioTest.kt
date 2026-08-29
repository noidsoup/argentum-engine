package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.core.ManaCost
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Hissing Iguanar (ALA #104 / PC2 #46) — whenever another creature dies, you may have it deal 1
 * damage to target player or planeswalker.
 */
class HissingIguanarScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(
            CardDefinition.creature("Small Blocker", ManaCost.parse("{1}"), emptySet(), power = 1, toughness = 1),
        )

        context("Hissing Iguanar") {

            fun advanceToDecision(game: TestGame) {
                var guard = 0
                while (!game.hasPendingDecision() && guard++ < 20) {
                    if (game.state.priorityPlayerId == null) break
                    game.passPriority()
                }
            }

            fun killAnotherCreature(game: TestGame) {
                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Lightning Bolt", bears).error shouldBe null
                game.resolveStack()
                game.isOnBattlefield("Grizzly Bears") shouldBe false
                advanceToDecision(game)
            }

            test("another creature dying may deal 1 damage to an opponent") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Hissing Iguanar")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lifeBefore = game.getLifeTotal(2)
                killAnotherCreature(game)

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true).error shouldBe null
                advanceToDecision(game)

                game.getPendingDecision().shouldBeInstanceOf<ChooseTargetsDecision>()
                game.selectTargets(listOf(game.player2Id)).error shouldBe null
                game.resolveStack()

                withClue("Hissing Iguanar dealt 1 damage to the opponent") {
                    game.getLifeTotal(2) shouldBe lifeBefore - 1
                }
            }

            test("declining the may ability deals no damage") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Hissing Iguanar")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lifeBefore = game.getLifeTotal(2)
                killAnotherCreature(game)

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("declining leaves opponent life unchanged") {
                    game.getLifeTotal(2) shouldBe lifeBefore
                }
            }

            test("does not trigger when Hissing Iguanar itself dies") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Hissing Iguanar")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val iguanar = game.findPermanent("Hissing Iguanar")!!
                game.castSpell(1, "Lightning Bolt", iguanar).error shouldBe null
                game.resolveStack()

                withClue("Hissing Iguanar is gone") {
                    game.isOnBattlefield("Hissing Iguanar") shouldBe false
                }
                withClue("no may-damage prompt when the source dies") {
                    game.hasPendingDecision() shouldBe false
                }
            }
        }
    }
}
