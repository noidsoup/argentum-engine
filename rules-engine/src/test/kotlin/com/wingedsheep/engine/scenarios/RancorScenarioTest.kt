package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ulg.cards.Rancor
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Rancor — When this Aura is put into a graveyard from the battlefield, return it to its owner's hand.
 */
class RancorScenarioTest : FunSpec({

    test("returns to hand when the enchanted creature dies") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(Rancor)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val player = driver.activePlayer!!
        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears")

        val rancor = driver.putCardInHand(player, "Rancor")
        driver.giveMana(player, Color.GREEN, 1)
        driver.castSpell(player, rancor, targets = listOf(bears))
        driver.bothPass()
        driver.findPermanent(player, "Rancor") shouldNotBe null

        val bolt = driver.putCardInHand(player, "Lightning Bolt")
        driver.giveMana(player, Color.RED, 1)
        driver.castSpell(player, bolt, targets = listOf(bears))
        driver.bothPass() // Bolt → bears die → Rancor to GY → return trigger
        driver.bothPass() // resolve return-to-hand

        driver.findCardInHand(player, "Rancor") shouldNotBe null
        driver.findPermanent(player, "Rancor") shouldBe null
    }
})
