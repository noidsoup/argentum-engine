package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.pc2.cards.BalefulStrix
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Baleful Strix — Flying, deathtouch; When this creature enters, draw a card.
 */
class BalefulStrixScenarioTest : FunSpec({

    test("draws a card when it enters") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(BalefulStrix)
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Swamp" to 20), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val player = driver.activePlayer!!
        val before = driver.getHandSize(player)
        val strix = driver.putCardInHand(player, "Baleful Strix")
        driver.giveMana(player, Color.BLUE, 1)
        driver.giveMana(player, Color.BLACK, 1)
        driver.castSpell(player, strix)
        driver.bothPass() // resolve spell → ETB trigger
        driver.bothPass() // resolve draw

        // before +1 (put in hand) -1 (cast) +1 (draw) = before + 1
        driver.getHandSize(player) shouldBe before + 1
        driver.findPermanent(player, "Baleful Strix") shouldNotBe null
    }
})
