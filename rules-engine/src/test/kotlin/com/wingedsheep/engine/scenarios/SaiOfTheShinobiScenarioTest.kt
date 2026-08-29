package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Sai of the Shinobi — +1/+1 equipped. Whenever a creature you control enters, you may attach this
 * Equipment to it. Equip {2}.
 */
class SaiOfTheShinobiScenarioTest : ScenarioTestBase() {

    init {
        context("Sai of the Shinobi") {

            test("when a creature enters you may attach Sai to it and grant +1/+1") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Sai of the Shinobi", summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as? YesNoDecision
                decision.shouldNotBeNull()
                game.answerYesNo(true)
                game.resolveStack()

                val bears = game.findPermanent("Grizzly Bears")!!
                val sai = game.findPermanent("Sai of the Shinobi")!!
                game.state.getEntity(sai)?.get<AttachedToComponent>()?.targetId shouldBe bears

                withClue("Sai grants +1/+1 to the equipped creature") {
                    game.state.projectedState.getPower(bears) shouldBe 3
                    game.state.projectedState.getToughness(bears) shouldBe 3
                }
            }

            test("declining the attach leaves Sai unattached") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Sai of the Shinobi", summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                val decision = game.getPendingDecision() as? YesNoDecision
                decision.shouldNotBeNull()
                game.answerYesNo(false)
                game.resolveStack()

                val sai = game.findPermanent("Sai of the Shinobi")!!
                withClue("Sai stays unattached when the player declines") {
                    game.state.getEntity(sai)?.get<AttachedToComponent>() shouldBe null
                }
            }
        }
    }
}
