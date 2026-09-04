package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Colorful Feiyi Sparrow — GS1 #2 · flying */
class ColorfulFeiyiSparrowScenarioTest : ScenarioTestBase() {

    init {
        test("has flying") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Colorful Feiyi Sparrow")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val sparrow = game.findPermanent("Colorful Feiyi Sparrow")!!

            withClue("Colorful Feiyi Sparrow has flying") {
                game.state.projectedState.hasKeyword(sparrow, Keyword.FLYING) shouldBe true
            }
        }
    }
}
