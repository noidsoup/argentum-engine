package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Cleansing Screech — GS1 #37 · deals 4 damage to any target */
class CleansingScreechScenarioTest : ScenarioTestBase() {

    init {
        test("deals 4 damage to target player") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Cleansing Screech")
                .withLandsOnBattlefield(1, "Mountain", 5)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpellTargetingPlayer(1, "Cleansing Screech", 2).error shouldBe null
            game.resolveStack()

            withClue("4 damage to the opponent") {
                game.getLifeTotal(2) shouldBe 16
            }
        }
    }
}
