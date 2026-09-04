package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Earthshaking Si — GS1 #31 · trample */
class EarthshakingSiScenarioTest : ScenarioTestBase() {

    init {
        test("has trample") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Earthshaking Si")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val si = game.findPermanent("Earthshaking Si")!!

            withClue("Earthshaking Si has trample") {
                game.state.projectedState.hasKeyword(si, Keyword.TRAMPLE) shouldBe true
            }
        }
    }
}
