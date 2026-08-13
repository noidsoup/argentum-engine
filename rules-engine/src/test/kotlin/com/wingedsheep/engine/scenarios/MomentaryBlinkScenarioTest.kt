package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.SummoningSicknessComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MomentaryBlinkScenarioTest : ScenarioTestBase() {
    init {
        test("exiles then returns a creature you control") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withCardInHand(1, "Momentary Blink")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            game.state.getEntity(bears)!!.has<SummoningSicknessComponent>() shouldBe false

            game.castSpell(1, "Momentary Blink", bears).error shouldBe null
            game.resolveStack()

            val blinked = game.findPermanent("Grizzly Bears")
            blinked shouldNotBe null
            withClue("blink re-enters with summoning sickness") {
                game.state.getEntity(blinked!!)!!.has<SummoningSicknessComponent>() shouldBe true
            }
        }
    }
}
