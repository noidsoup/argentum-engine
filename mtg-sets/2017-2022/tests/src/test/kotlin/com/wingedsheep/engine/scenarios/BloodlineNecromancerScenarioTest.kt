package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Bloodline Necromancer (C17 #14) — {4}{B} Creature — Vampire Wizard 3/2
 *
 * Lifelink
 * When this creature enters, you may return target Vampire or Wizard creature card from your
 * graveyard to the battlefield.
 */
class BloodlineNecromancerScenarioTest : ScenarioTestBase() {

    init {
        context("Bloodline Necromancer") {

            test("ETB may return a Vampire creature card from your graveyard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Bloodline Necromancer")
                    .withCardInGraveyard(1, "Vampire Nighthawk")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Bloodline Necromancer").error shouldBe null
                game.resolveStack()

                withClue("consent to the optional return") {
                    game.hasPendingDecision() shouldBe true
                    game.answerYesNo(true)
                }
                game.resolveStack()

                val nighthawk = game.findCardsInGraveyard(1, "Vampire Nighthawk").single()
                game.selectTargets(listOf(nighthawk))
                game.resolveStack()

                withClue("Vampire Nighthawk is reanimated onto the battlefield") {
                    game.isOnBattlefield("Vampire Nighthawk") shouldBe true
                }
            }

            test("a non-Vampire non-Wizard creature card is not a legal target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Bloodline Necromancer")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Bloodline Necromancer").error shouldBe null
                game.resolveStack()

                if (game.hasPendingDecision()) {
                    game.answerYesNo(true)
                    game.resolveStack()
                }

                val bears = game.findCardsInGraveyard(1, "Grizzly Bears").single()
                if (game.hasPendingDecision()) {
                    withClue("Grizzly Bears is neither Vampire nor Wizard") {
                        game.selectTargets(listOf(bears)).error shouldNotBe null
                    }
                    game.skipTargets()
                    game.resolveStack()
                }

                withClue("Grizzly Bears stays in the graveyard") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
            }
        }
    }
}
