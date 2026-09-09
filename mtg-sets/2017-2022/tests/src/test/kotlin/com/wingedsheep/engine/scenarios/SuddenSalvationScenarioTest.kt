package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Sudden Salvation (VOC #10) — {2}{W}{W} Instant.
 *
 * Choose up to three target permanent cards in graveyards that were put there from the battlefield
 * this turn. Return them to the battlefield tapped under their owners' control. You draw a card for
 * each opponent who controls one or more of those permanents.
 */
class SuddenSalvationScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Plains" to 30))
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.settle() {
        var guard = 0
        while (!state.gameOver && state.stack.isNotEmpty() && guard++ < 20) bothPass()
    }

    fun GameTestDriver.bolt(target: com.wingedsheep.sdk.model.EntityId, caster: com.wingedsheep.sdk.model.EntityId) {
        val bolt = putCardInHand(caster, "Lightning Bolt")
        giveMana(caster, Color.RED, 1)
        castSpell(caster, bolt, targets = listOf(target)).error shouldBe null
        settle()
    }

    test("returns a permanent that died this turn tapped under its owner's control and draws when an opponent controls it") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)

        val opponentBear = d.putCreatureOnBattlefield(p2, "Grizzly Bears")
        d.bolt(opponentBear, p1)
        val graveyardBear = d.getGraveyard(p2).single { d.getCardName(it) == "Grizzly Bears" }
        withClue("the Bears died into the opponent's graveyard") {
            (graveyardBear in d.getGraveyard(p2)) shouldBe true
        }

        val handBefore = d.getHandSize(p1)
        val salvation = d.putCardInHand(p1, "Sudden Salvation")
        d.giveMana(p1, Color.WHITE, 4)
        d.castSpellWithTargets(
            p1,
            salvation,
            listOf(ChosenTarget.Card(graveyardBear, p2, Zone.GRAVEYARD)),
        ).error shouldBe null
        d.settle()

        val returned = d.findPermanent(p2, "Grizzly Bears").shouldNotBeNull()
        withClue("the returned permanent is under its owner's control") {
            d.getController(returned) shouldBe p2
        }
        withClue("the returned permanent enters tapped") {
            d.isTapped(returned) shouldBe true
        }
        withClue("the caster draws because an opponent controls a returned permanent") {
            d.getHandSize(p1) shouldBe handBefore + 1
        }
    }

    test("a permanent card that never left the battlefield this turn is not a legal target") {
        val d = driver()
        val p1 = d.activePlayer!!

        val milled = d.putCardInGraveyard(p1, "Grizzly Bears")
        val salvation = d.putCardInHand(p1, "Sudden Salvation")
        d.giveMana(p1, Color.WHITE, 4)

        val cast = d.castSpellWithTargets(
            p1,
            salvation,
            listOf(ChosenTarget.Card(milled, p1, Zone.GRAVEYARD)),
        )
        withClue("a card merely in the graveyard is outside the filter") {
            cast.error shouldNotBe null
        }
    }
})
