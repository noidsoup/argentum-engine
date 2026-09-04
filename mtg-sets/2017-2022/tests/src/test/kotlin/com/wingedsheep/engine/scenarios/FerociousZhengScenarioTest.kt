package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Ferocious Zheng — GS1 #28 · 4/4 vanilla */
class FerociousZhengScenarioTest : ScenarioTestBase() {

    init {
        test("is a 4/4 creature with no abilities") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Ferocious Zheng")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val zheng = game.findPermanent("Ferocious Zheng")!!

            withClue("printed stats") {
                game.state.projectedState.getPower(zheng) shouldBe 4
                game.state.projectedState.getToughness(zheng) shouldBe 4
            }
        }
    }
}
