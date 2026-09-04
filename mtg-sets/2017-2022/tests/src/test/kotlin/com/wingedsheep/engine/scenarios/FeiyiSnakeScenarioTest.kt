package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Feiyi Snake — GS1 #24 · reach */
class FeiyiSnakeScenarioTest : ScenarioTestBase() {

    init {
        test("has reach") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Feiyi Snake")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val snake = game.findPermanent("Feiyi Snake")!!

            withClue("Feiyi Snake has reach") {
                game.state.projectedState.hasKeyword(snake, Keyword.REACH) shouldBe true
            }
        }
    }
}
