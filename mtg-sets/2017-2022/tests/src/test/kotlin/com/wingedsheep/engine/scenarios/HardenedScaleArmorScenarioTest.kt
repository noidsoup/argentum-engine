package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Hardened-Scale Armor — GS1 #32 · enchanted creature gets +3/+3 */
class HardenedScaleArmorScenarioTest : ScenarioTestBase() {

    init {
        test("enchanted creature gets +3/+3") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Hardened-Scale Armor")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Forest", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bear = game.findPermanent("Grizzly Bears")!!

            game.castSpell(1, "Hardened-Scale Armor", bear).error shouldBe null
            game.resolveStack()

            withClue("Grizzly Bears gets +3/+3") {
                game.state.projectedState.getPower(bear) shouldBe 5
                game.state.projectedState.getToughness(bear) shouldBe 5
            }
        }
    }
}
