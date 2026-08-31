package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Moon-Eating Dog — Global Series: Jiang Yanggu & Mu Yanling #10
 * {3}{U} Creature — Dog, 3/3
 *
 * As long as you control a Yanling planeswalker, this creature has flying.
 */
class MoonEatingDogScenarioTest : ScenarioTestBase() {

    init {
        test("has no flying without a Yanling planeswalker") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Moon-Eating Dog")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val dog = game.findPermanent("Moon-Eating Dog")!!

            withClue("no Yanling planeswalker means no flying") {
                game.state.projectedState.hasKeyword(dog, Keyword.FLYING) shouldBe false
            }
        }

        test("has flying while you control Mu Yanling") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Moon-Eating Dog")
                .withCardOnBattlefield(1, "Mu Yanling")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val dog = game.findPermanent("Moon-Eating Dog")!!

            withClue("controlling Mu Yanling grants flying") {
                game.state.projectedState.hasKeyword(dog, Keyword.FLYING) shouldBe true
            }
        }
    }
}
