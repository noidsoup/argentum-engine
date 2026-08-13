package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.avr.cards.EmancipationAngel
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class EmancipationAngelScenarioTest : FunSpec({
    test("ETB returns a permanent you control to hand") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(EmancipationAngel)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        val beforeHand = driver.getHandSize(you)
        val card = driver.putCardInHand(you, "Emancipation Angel")
        driver.giveMana(you, Color.WHITE, 2)
        driver.giveColorlessMana(you, 1)
        driver.castSpell(you, card)
        driver.bothPass() // resolve angel — may prompt target

        val decision = driver.pendingDecision
        if (decision != null) {
            driver.submitTargetSelection(you, listOf(bears))
        }
        driver.bothPass()

        driver.findPermanent(you, "Emancipation Angel") shouldNotBe null
        driver.findPermanent(you, "Grizzly Bears") shouldBe null
        // put(+1) cast(-1) bounce(+1) => beforeHand + 1
        driver.getHandSize(you) shouldBe beforeHand + 1
    }
})
