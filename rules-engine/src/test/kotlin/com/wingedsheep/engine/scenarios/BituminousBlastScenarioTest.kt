package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.arb.cards.BituminousBlast
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Bituminous Blast — Cascade + deals 4 damage to target creature.
 *
 * Proves the damage half; cascade uses the shared Effects.Cascade executor already covered by
 * Bloodbraid Elf / Maelstrom Wanderer cast-trigger wiring.
 */
class BituminousBlastScenarioTest : FunSpec({

    test("deals 4 damage to target creature") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(BituminousBlast)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        val bears = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears") // 2/2

        val blast = driver.putCardInHand(player, "Bituminous Blast")
        driver.giveMana(player, Color.BLACK, 1)
        driver.giveMana(player, Color.RED, 1)
        driver.giveColorlessMana(player, 3)
        driver.castSpell(player, blast, targets = listOf(bears))
        // Cascade trigger may stack above the spell; pass until the stack clears.
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) {
            driver.bothPass()
        }

        driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
        driver.getGraveyardCardNames(opponent).contains("Grizzly Bears") shouldBe true
    }
})
