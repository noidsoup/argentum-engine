package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ToothCollectorScenarioTest : ScenarioTestBase() {
    init {
        test("ETB gives opponent creature -1/-1 until end of turn") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardInHand(1, "Tooth Collector")
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            game.castSpell(1, "Tooth Collector", bears).error shouldBe null
            game.resolveStack()
            // ETB may need a second resolve if target wasn't bound at cast time
            if (game.state.pendingDecision != null) {
                game.selectTargets(listOf(bears))
            }
            if (game.state.stack.isNotEmpty()) game.resolveStack()

            game.findPermanent("Tooth Collector") shouldNotBe null
            game.state.projectedState.getPower(bears) shouldBe 1
            game.state.projectedState.getToughness(bears) shouldBe 1
        }
    }
}
