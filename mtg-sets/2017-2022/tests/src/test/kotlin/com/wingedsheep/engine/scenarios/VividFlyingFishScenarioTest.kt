package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gs1.cards.VividFlyingFish
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class VividFlyingFishScenarioTest : FunSpec({
    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(VividFlyingFish)
        return driver
    }

    test("has flying only while attacking") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 30), startingLife = 20)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        val fish = driver.putCreatureOnBattlefield(me, "Vivid Flying Fish")
        driver.removeSummoningSickness(fish)

        projector.project(driver.state).hasKeyword(fish, Keyword.FLYING) shouldBe false

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(me, listOf(fish), opponent)

        projector.project(driver.state).hasKeyword(fish, Keyword.FLYING) shouldBe true
    }
})
