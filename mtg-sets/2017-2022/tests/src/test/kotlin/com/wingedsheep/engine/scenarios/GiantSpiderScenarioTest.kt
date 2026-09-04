package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Giant Spider — GS1 reprint · reach */
class GiantSpiderScenarioTest : ScenarioTestBase() {

    init {
        test("has reach") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Giant Spider")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val spider = game.findPermanent("Giant Spider")!!

            withClue("Giant Spider has reach") {
                game.state.projectedState.hasKeyword(spider, Keyword.REACH) shouldBe true
            }
        }
    }
}
