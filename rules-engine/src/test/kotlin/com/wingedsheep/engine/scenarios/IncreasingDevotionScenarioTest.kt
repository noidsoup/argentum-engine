package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe

class IncreasingDevotionScenarioTest : ScenarioTestBase() {
    init {
        test("creates five 1/1 Human tokens") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardInHand(1, "Increasing Devotion")
                .withLandsOnBattlefield(1, "Plains", 5)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Increasing Devotion").error shouldBe null
            game.resolveStack()

            game.state.getZone(game.player1Id, Zone.BATTLEFIELD).size shouldBeGreaterThanOrEqual 10
            // 5 Plains + 5 Human tokens
            game.state.getZone(game.player1Id, Zone.BATTLEFIELD).size shouldBe 10
        }
    }
}
