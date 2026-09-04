package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Armored Whirl Turtle — GS1 #7 · 0/5 vanilla */
class ArmoredWhirlTurtleScenarioTest : ScenarioTestBase() {

    init {
        test("is a 0/5 creature with no abilities") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Armored Whirl Turtle")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val turtle = game.findPermanent("Armored Whirl Turtle")!!

            withClue("printed stats") {
                game.state.projectedState.getPower(turtle) shouldBe 0
                game.state.projectedState.getToughness(turtle) shouldBe 5
            }
        }
    }
}
