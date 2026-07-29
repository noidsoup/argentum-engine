package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gs1.cards.CleansingScreech
import com.wingedsheep.mtg.sets.definitions.gs1.cards.ConfidenceFromStrength
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * GS1 Extra batch 02 — Cleansing Screech (4 to any target) and
 * Confidence from Strength (+4/+4 and trample until EOT).
 */
class Gs1ExtraBatch02ScenarioTest : FunSpec({

    fun driver(vararg extras: com.wingedsheep.sdk.model.CardDefinition): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + extras.toList())
        d.initMirrorMatch(Deck.of("Mountain" to 20, "Forest" to 20), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("Cleansing Screech: deals 4 damage to a creature") {
        val d = driver(CleansingScreech)
        val bears = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears") // 2/2
        val spell = d.putCardInHand(d.player1, "Cleansing Screech")
        d.giveMana(d.player1, Color.RED, 1)
        d.giveColorlessMana(d.player1, 4)
        d.castSpell(d.player1, spell, listOf(bears)).isSuccess shouldBe true
        d.bothPass()

        d.findPermanent(d.player2, "Grizzly Bears") shouldBe null
    }

    test("Confidence from Strength: +4/+4 and trample until end of turn") {
        val d = driver(ConfidenceFromStrength)
        val bears = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears") // 2/2
        d.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe false

        val spell = d.putCardInHand(d.player1, "Confidence from Strength")
        d.giveMana(d.player1, Color.GREEN, 1)
        d.giveColorlessMana(d.player1, 2)
        d.castSpell(d.player1, spell, listOf(bears)).isSuccess shouldBe true
        d.bothPass()

        d.state.projectedState.getPower(bears) shouldBe 6
        d.state.projectedState.getToughness(bears) shouldBe 6
        d.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe true
    }
})
