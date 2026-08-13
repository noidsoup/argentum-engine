package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.avr.cards.CathedralSanctifier
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** Cathedral Sanctifier: ETB gain 3 life. */
class CathedralSanctifierScenarioTest : FunSpec({

    test("entering the battlefield gains 3 life") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CathedralSanctifier)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)

        val you = driver.activePlayer!!
        driver.putCreatureOnBattlefield(you, "Cathedral Sanctifier")
        driver.bothPass()

        driver.getLifeTotal(you) shouldBe 23
    }
})
