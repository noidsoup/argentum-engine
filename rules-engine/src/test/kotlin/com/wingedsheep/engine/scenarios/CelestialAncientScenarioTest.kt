package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Celestial Ancient (DIS #7 / PC2 #5) — flying elemental that bolsters your board when you cast
 * enchantments.
 */
class CelestialAncientScenarioTest : ScenarioTestBase() {

    init {
        test("casting an enchantment puts a +1/+1 counter on each creature you control") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Celestial Ancient")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardInHand(1, "Glorious Anthem")
                .withLandsOnBattlefield(1, "Plains", 5)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Glorious Anthem").error shouldBe null
            game.resolveStack()

            val ancient = game.findPermanent("Celestial Ancient")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            withClue("Celestial Ancient gets a counter") {
                game.state.getEntity(ancient)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
            }
            withClue("Grizzly Bears gets a counter") {
                game.state.getEntity(bears)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
            }
        }
    }
}
