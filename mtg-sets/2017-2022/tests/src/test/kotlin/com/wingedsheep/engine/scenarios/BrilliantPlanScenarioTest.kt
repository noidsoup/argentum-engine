package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Brilliant Plan — GS1 reprint · draw three cards */
class BrilliantPlanScenarioTest : ScenarioTestBase() {

    init {
        test("draws three cards") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Brilliant Plan")
                .withLandsOnBattlefield(1, "Island", 5)
                .withCardInLibrary(1, "Grizzly Bears")
                .withCardInLibrary(1, "Hill Giant")
                .withCardInLibrary(1, "Lightning Bolt")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val handBefore = game.handSize(1)

            game.castSpell(1, "Brilliant Plan").error shouldBe null
            game.resolveStack()

            withClue("spell left hand and three cards were drawn") {
                game.handSize(1) shouldBe handBefore - 1 + 3
            }
        }
    }
}
