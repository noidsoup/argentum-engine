package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Bloodlord of Vaasgoth (M12 #82) — {3}{B}{B} Creature — Vampire Warrior 3/3
 *
 * Bloodthirst 3
 * Flying
 * Whenever you cast a Vampire creature spell, it gains bloodthirst 3.
 */
class BloodlordOfVaasgothScenarioTest : ScenarioTestBase() {

    init {
        context("Bloodlord of Vaasgoth") {

            fun projectedPowerToughness(game: TestGame, name: String): Pair<Int, Int> {
                val permanent = game.findPermanent(name)!!
                return Pair(
                    game.state.projectedState.getPower(permanent)!!,
                    game.state.projectedState.getToughness(permanent)!!,
                )
            }

            fun castBoltAtOpponent(game: TestGame) {
                game.castSpellTargetingPlayer(1, "Lightning Bolt", 2).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()
            }

            test("enters with three +1/+1 counters when an opponent was dealt damage this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Bloodlord of Vaasgoth")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castBoltAtOpponent(game)

                game.castSpell(1, "Bloodlord of Vaasgoth").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("3/3 plus three bloodthirst counters") {
                    projectedPowerToughness(game, "Bloodlord of Vaasgoth") shouldBe Pair(6, 6)
                }
            }

            test("grants bloodthirst 3 to Vampire creature spells you cast") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Bloodlord of Vaasgoth")
                    .withCardInHand(1, "Vampire Nighthawk")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Swamp", 10)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Bloodlord of Vaasgoth").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                castBoltAtOpponent(game)

                game.castSpell(1, "Vampire Nighthawk").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("2/3 plus three bloodthirst counters") {
                    projectedPowerToughness(game, "Vampire Nighthawk") shouldBe Pair(5, 6)
                }
            }
        }
    }
}
