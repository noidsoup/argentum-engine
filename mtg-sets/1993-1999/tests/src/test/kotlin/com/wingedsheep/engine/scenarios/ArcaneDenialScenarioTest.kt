package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.all.cards.ArcaneDenial
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Arcane Denial — counter target spell; at the beginning of the next turn's upkeep its controller
 * may draw up to two cards and you draw a card.
 */
class ArcaneDenialScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + ArcaneDenial)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.counterWithDenial(responder: EntityId, spellOnStack: EntityId) {
        if (priorityPlayer != responder) passPriority(getOpponent(responder))
        giveMana(responder, Color.BLUE, 2)
        val denial = putCardInHand(responder, "Arcane Denial")
        castSpellWithTargets(responder, denial, listOf(ChosenTarget.Spell(spellOnStack))).isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && guard++ < 20) bothPass()
    }

    /** Next upkeep after the current turn — stop before the draw step so hand size isn't polluted. */
    fun GameTestDriver.advanceToNextUpkeep() {
        passPriorityUntil(Step.END)
        passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        var guard = 0
        while (stackSize > 0 && guard++ < 20) bothPass()
    }

    test("counters the spell and schedules two delayed upkeep draws") {
        val d = driver()
        val responder = d.player2
        val victim = d.player1

        d.giveMana(victim, Color.GREEN, 3)
        val courser = d.putCardInHand(victim, "Centaur Courser")
        d.castSpell(victim, courser).isSuccess shouldBe true

        d.counterWithDenial(responder, courser)

        withClue("the creature spell was countered") {
            d.getGraveyardCardNames(victim).contains("Centaur Courser") shouldBe true
            d.findPermanent(victim, "Centaur Courser") shouldBe null
        }
        withClue("one delayed trigger for the victim's may-draw and one for the caster's draw") {
            d.state.delayedTriggers.size shouldBe 2
        }
    }

    test("at the next turn's upkeep the caster draws and the victim auto-declines the may") {
        val d = driver()
        val responder = d.player2
        val victim = d.player1

        d.giveMana(victim, Color.GREEN, 3)
        val courser = d.putCardInHand(victim, "Centaur Courser")
        d.castSpell(victim, courser).isSuccess shouldBe true
        d.counterWithDenial(responder, courser)

        val victimHandBefore = d.getHandSize(victim)
        val responderHandBefore = d.getHandSize(responder)

        d.advanceToNextUpkeep()

        withClue("Arcane Denial's controller draws one at the next turn's upkeep") {
            d.getHandSize(responder) shouldBe responderHandBefore + 1
        }
        withClue("the countered spell's controller auto-declines the may draw") {
            d.getHandSize(victim) shouldBe victimHandBefore
        }
    }
})
