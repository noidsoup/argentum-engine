package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Stormcloud Spirit — Global Series: Jiang Yanggu & Mu Yanling #11
 * {3}{U}{U} Creature — Spirit, 4/4
 *
 * Flying
 */
class StormcloudSpiritScenarioTest : ScenarioTestBase() {

    init {
        test("has flying") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Stormcloud Spirit")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val spirit = game.findPermanent("Stormcloud Spirit")!!

            withClue("Stormcloud Spirit has flying") {
                game.state.projectedState.hasKeyword(spirit, Keyword.FLYING) shouldBe true
            }
        }
    }
}
