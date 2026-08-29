package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.apc.cards.WhirlpoolWarrior
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Whirlpool Warrior (APC {2}{U} 2/2).
 *
 * ETB: controller wheels their hand; {R}, Sacrifice: each player wheels.
 */
class WhirlpoolWarriorScenarioTest : ScenarioTestBase() {

    init {
        val wheelAllId = WhirlpoolWarrior.activatedAbilities[0].id

        context("Whirlpool Warrior") {
            test("ETB shuffles controller hand into library then draws that many cards") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Whirlpool Warrior")
                    .withCardsInHand(1, "Lightning Bolt", 3)
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("four cards in hand before cast (Warrior + three bolts)") {
                    game.handSize(1) shouldBe 4
                }

                game.castSpell(1, "Whirlpool Warrior").error shouldBe null
                game.resolveStack()

                withClue("ETB wheels the three remaining hand cards then redraws three") {
                    game.handSize(1) shouldBe 3
                }
                withClue("Warrior is on the battlefield") {
                    game.findPermanent("Whirlpool Warrior").shouldNotBeNull()
                }
            }

            test("{R}, Sacrifice: each player wheels their hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Whirlpool Warrior", summoningSickness = false)
                    .withCardsInHand(1, "Lightning Bolt", 2)
                    .withCardsInHand(2, "Grizzly Bears", 3)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val warrior = game.findPermanent("Whirlpool Warrior")!!
                withClue("setup hand sizes") {
                    game.handSize(1) shouldBe 2
                    game.handSize(2) shouldBe 3
                }

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = warrior,
                        abilityId = wheelAllId,
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("Warrior is sacrificed") {
                    game.findPermanent("Whirlpool Warrior") shouldBe null
                }
                withClue("each player drew back to their pre-wheel hand size") {
                    game.handSize(1) shouldBe 2
                    game.handSize(2) shouldBe 3
                }
            }
        }
    }
}
