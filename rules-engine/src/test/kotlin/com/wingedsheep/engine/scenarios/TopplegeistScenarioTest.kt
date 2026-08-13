package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ddq.cards.Topplegeist
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TopplegeistScenarioTest : FunSpec({
    test("ETB taps target creature an opponent controls") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(Topplegeist)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        val opp = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        val card = driver.putCardInHand(you, "Topplegeist")
        driver.giveMana(you, Color.WHITE, 1)
        driver.castSpell(you, card)
        driver.bothPass() // resolve Topplegeist
        val decision = driver.pendingDecision
        decision shouldNotBe null
        driver.submitTargetSelection(you, listOf(bears))
        driver.bothPass()

        driver.isTapped(bears) shouldBe true
        driver.findPermanent(you, "Topplegeist") shouldNotBe null
    }
})
