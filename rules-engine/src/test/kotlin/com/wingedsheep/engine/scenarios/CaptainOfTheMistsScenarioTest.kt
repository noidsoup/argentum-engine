package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.avr.cards.CaptainOfTheMists
import com.wingedsheep.mtg.sets.definitions.avr.cards.CathedralSanctifier
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class CaptainOfTheMistsScenarioTest : FunSpec({
    test("another Human entering untaps the Captain") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CaptainOfTheMists)
        driver.registerCard(CathedralSanctifier)
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 20), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val captainId = driver.putPermanentOnBattlefield(you, "Captain of the Mists")
        driver.tapPermanent(captainId)
        driver.isTapped(captainId) shouldBe true

        val human = driver.putCardInHand(you, "Cathedral Sanctifier")
        driver.giveMana(you, Color.WHITE, 1)
        driver.castSpell(you, human)
        driver.bothPass() // resolve Human
        driver.bothPass() // resolve one ETB trigger
        driver.bothPass() // resolve the other ETB trigger

        driver.isTapped(captainId) shouldBe false
        driver.findPermanent(you, "Captain of the Mists") shouldNotBe null
        driver.findPermanent(you, "Cathedral Sanctifier") shouldNotBe null
    }
})
