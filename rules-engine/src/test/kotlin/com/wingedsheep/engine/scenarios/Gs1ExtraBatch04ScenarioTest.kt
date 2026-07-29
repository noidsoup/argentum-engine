package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gs1.cards.EarthOriginYak
import com.wingedsheep.mtg.sets.definitions.gs1.cards.EarthshakingSi
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * GS1 Extra batch 04 — Earth-Origin Yak (ETB team pump) and Earthshaking Si (trample).
 */
class Gs1ExtraBatch04ScenarioTest : FunSpec({

    fun driver(vararg extras: com.wingedsheep.sdk.model.CardDefinition): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + extras.toList())
        d.initMirrorMatch(Deck.of("Plains" to 20, "Forest" to 20), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("Earth-Origin Yak: ETB gives creatures you control +1/+1 until EOT") {
        val d = driver(EarthOriginYak)
        val bears = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears") // 2/2
        d.state.projectedState.getPower(bears) shouldBe 2

        val yak = d.putCardInHand(d.player1, "Earth-Origin Yak")
        d.giveMana(d.player1, Color.WHITE, 1)
        d.giveColorlessMana(d.player1, 3)
        d.castSpell(d.player1, yak).isSuccess shouldBe true
        d.bothPass() // resolve spell → ETB trigger
        d.bothPass() // resolve trigger

        val yakId = d.findPermanent(d.player1, "Earth-Origin Yak")!!
        d.state.projectedState.getPower(bears) shouldBe 3
        d.state.projectedState.getToughness(bears) shouldBe 3
        d.state.projectedState.getPower(yakId) shouldBe 3
        d.state.projectedState.getToughness(yakId) shouldBe 5
    }

    test("Earthshaking Si: 5/5 with trample") {
        val d = driver(EarthshakingSi)
        val si = d.putCreatureOnBattlefield(d.player1, "Earthshaking Si")
        d.state.projectedState.getPower(si) shouldBe 5
        d.state.projectedState.getToughness(si) shouldBe 5
        d.state.projectedState.hasKeyword(si, Keyword.TRAMPLE) shouldBe true
    }
})
