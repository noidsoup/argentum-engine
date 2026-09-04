package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Leopard-Spotted Jiao — GS1 #23 · 3/1 vanilla */
class LeopardSpottedJiaoScenarioTest : ScenarioTestBase() {

    init {
        test("is a 3/1 creature with no abilities") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Leopard-Spotted Jiao")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val jiao = game.findPermanent("Leopard-Spotted Jiao")!!

            withClue("printed stats") {
                game.state.projectedState.getPower(jiao) shouldBe 3
                game.state.projectedState.getToughness(jiao) shouldBe 1
            }
        }
    }
}
