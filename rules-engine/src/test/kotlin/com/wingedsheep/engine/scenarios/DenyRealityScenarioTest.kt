package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.arb.cards.DenyReality
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Deny Reality — Cascade + return target permanent to its owner's hand.
 *
 * Proves the bounce half; cascade uses the shared Effects.Cascade executor already covered by
 * Bloodbraid Elf / Bituminous Blast cast-trigger wiring.
 */
class DenyRealityScenarioTest : FunSpec({

    test("returns target permanent to its owner's hand") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(DenyReality)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        val bears = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val deny = driver.putCardInHand(player, "Deny Reality")
        driver.giveMana(player, Color.BLUE, 1)
        driver.giveMana(player, Color.BLACK, 1)
        driver.giveColorlessMana(player, 3)
        driver.castSpell(player, deny, targets = listOf(bears))

        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) {
            driver.bothPass()
        }

        driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
        driver.getHand(opponent).contains(bears) shouldBe true
    }
})
