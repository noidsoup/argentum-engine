package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gs1.cards.DragonsPresence
import com.wingedsheep.mtg.sets.definitions.gs1.cards.DrownInShapelessness
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * GS1 Extra batch 03 — Dragon's Presence (5 to attacking/blocking) and
 * Drown in Shapelessness (bounce creature).
 */
class Gs1ExtraBatch03ScenarioTest : FunSpec({

    fun driver(vararg extras: com.wingedsheep.sdk.model.CardDefinition): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + extras.toList())
        d.initMirrorMatch(
            Deck.of("Plains" to 15, "Island" to 15, "Mountain" to 10),
            skipMulligans = true,
            startingPlayer = 0,
        )
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("Drown in Shapelessness: returns target creature to hand") {
        val d = driver(DrownInShapelessness)
        val bears = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val beforeHand = d.getHandSize(d.player2)

        val spell = d.putCardInHand(d.player1, "Drown in Shapelessness")
        d.giveMana(d.player1, Color.BLUE, 1)
        d.giveColorlessMana(d.player1, 1)
        d.castSpell(d.player1, spell, listOf(bears)).isSuccess shouldBe true
        d.bothPass()

        d.findPermanent(d.player2, "Grizzly Bears") shouldBe null
        d.getHandSize(d.player2) shouldBe beforeHand + 1
        d.state.getZone(d.player2, Zone.HAND).contains(bears) shouldBe true
    }

    test("Dragon's Presence: deals 5 to an attacking creature") {
        val d = driver(DragonsPresence)
        val attacker = d.putCreatureOnBattlefield(d.player1, "Hill Giant") // 3/3
        d.removeSummoningSickness(attacker)
        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, listOf(attacker), d.player2).error shouldBe null

        // After attackers are declared, active player has priority — cast from player1.
        val spell = d.putCardInHand(d.player1, "Dragon's Presence")
        d.giveMana(d.player1, Color.WHITE, 1)
        d.giveColorlessMana(d.player1, 2)
        d.castSpell(d.player1, spell, listOf(attacker)).error shouldBe null
        d.bothPass()

        d.findPermanent(d.player1, "Hill Giant") shouldBe null
    }
})
