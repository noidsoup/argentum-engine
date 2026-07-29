package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gs1.cards.AggressiveInstinct
import com.wingedsheep.mtg.sets.definitions.ptk.cards.BrilliantPlan
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * GS1 Extra batch 01 — Aggressive Instinct (one-sided power damage) and Brilliant Plan (draw 3).
 *
 * Aggressive Instinct must target youControl then opponentControls; a wrong AUTOGEN that used
 * youControl twice would either fail to cast or never deal damage to the opponent's creature.
 */
class Gs1ExtraBatch01ScenarioTest : FunSpec({

    fun driver(vararg extras: com.wingedsheep.sdk.model.CardDefinition): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + extras.toList())
        d.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("Aggressive Instinct: 3-power creature deals 3 to opposing 2/2; dealer takes nothing") {
        val d = driver(AggressiveInstinct)
        val yours = d.putCreatureOnBattlefield(d.player1, "Hill Giant") // 3/3
        val theirs = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears") // 2/2

        val spell = d.putCardInHand(d.player1, "Aggressive Instinct")
        d.giveMana(d.player1, Color.GREEN, 1)
        d.giveColorlessMana(d.player1, 1)
        d.castSpell(d.player1, spell, listOf(yours, theirs)).isSuccess shouldBe true
        d.bothPass()

        d.findPermanent(d.player2, "Grizzly Bears") shouldBe null
        d.findPermanent(d.player1, "Hill Giant") shouldBe yours
    }

    test("Brilliant Plan: draw three cards") {
        val d = driver(BrilliantPlan)
        val before = d.getHandSize(d.player1)
        val spell = d.putCardInHand(d.player1, "Brilliant Plan")
        d.giveMana(d.player1, Color.BLUE, 1)
        d.giveColorlessMana(d.player1, 4)
        d.castSpell(d.player1, spell).isSuccess shouldBe true
        d.bothPass()

        // +1 from putting the spell in hand, −1 when cast, +3 from resolve → net +3 vs before
        d.getHandSize(d.player1) shouldBe before + 3
    }
})
