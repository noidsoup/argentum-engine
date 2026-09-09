package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.ChoiceSlot
import com.wingedsheep.sdk.scripting.conditions.MadnessCostWasPaid
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Tests for [Conditions.MadnessCostWasPaid] (CR 702.35).
 *
 * Riders like Avacyn's Judgment branch on whether "this spell's madness cost was paid". The
 * condition reads the durable `ChoiceSlot.MADNESS_CAST` flag on a resolved permanent, falling back
 * to the resolution context (`wasMadness`) for a non-permanent spell's own effect — mirroring
 * [Conditions.MayhemCostWasPaid] / [Conditions.SneakCostWasPaid].
 */
class MadnessCostWasPaidMechanicScenarioTest : FunSpec({

    // A madness SORCERY whose effect branches on whether the madness cost was paid (mirrors
    // Avacyn's Judgment's rider shape). Gain 5 life if madness-cast, else 2.
    val madnessBolt = card("Madness Rider Bolt") {
        manaCost = "{2}{R}"
        typeLine = "Sorcery"
        spell {
            effect = ConditionalEffect(
                condition = Conditions.MadnessCostWasPaid,
                effect = Effects.GainLife(5),
                elseEffect = Effects.GainLife(2)
            )
        }
        madness("{R}")
    }

    // A madness creature to prove the durable flag survives on a resolving permanent.
    val madnessBeast = card("Madness Rider Beast") {
        manaCost = "{4}{R}"
        typeLine = "Creature — Beast"
        power = 3
        toughness = 3
        madness("{R}")
    }

    val selfDiscard = card("Madness Rider Discard") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        spell { effect = Effects.Discard(1) }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(madnessBolt, madnessBeast, selfDiscard)
        )
        return driver
    }

    fun discardViaSpell(driver: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId, cardId: com.wingedsheep.sdk.model.EntityId) {
        val spell = driver.putCardInHand(player, "Madness Rider Discard")
        driver.giveColorlessMana(player, 1)
        driver.submitSuccess(CastSpell(player, spell, paymentStrategy = PaymentStrategy.FromPool))
        driver.bothPass()
        driver.submitCardSelection(player, listOf(cardId))
    }

    fun settle(driver: GameTestDriver) {
        var guard = 0
        while (driver.state.stack.isNotEmpty() && driver.state.pendingDecision == null && guard++ < 20) {
            driver.bothPass()
        }
    }

    test("madness-cast sorcery resolves its madness branch and returns to the graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bolt = driver.putCardInHand(player, "Madness Rider Bolt")
        val lifeBefore = driver.getLifeTotal(player)
        discardViaSpell(driver, player, bolt)

        driver.giveMana(player, Color.RED, 1)
        settle(driver)
        driver.submitYesNo(player, true)
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getLifeTotal(player) shouldBe lifeBefore + 5
        driver.getGraveyard(player) shouldContain bolt
    }

    test("casting the sorcery normally from hand does NOT trigger the madness-paid branch") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bolt = driver.putCardInHand(player, "Madness Rider Bolt")
        driver.giveMana(player, Color.RED, 3)
        val lifeBefore = driver.getLifeTotal(player)

        driver.submitSuccess(
            CastSpell(playerId = player, cardId = bolt, paymentStrategy = PaymentStrategy.FromPool)
        )
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getLifeTotal(player) shouldBe lifeBefore + 2
    }

    test("madness-cast permanent enters the battlefield and carries the madness-paid flag") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val beast = driver.putCardInHand(player, "Madness Rider Beast")
        discardViaSpell(driver, player, beast)
        driver.giveMana(player, Color.RED, 1)
        settle(driver)
        driver.submitYesNo(player, true)
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        val perm = driver.findPermanent(player, "Madness Rider Beast")
        perm.shouldNotBeNull()
        driver.state.getEntity(perm)?.get<CastChoicesComponent>()?.chosen?.containsKey(ChoiceSlot.MADNESS_CAST) shouldBe true
        ConditionEvaluator().evaluate(
            driver.state,
            MadnessCostWasPaid,
            EffectContext(sourceId = perm, controllerId = player)
        ).shouldBeTrue()
    }

    test("declining the madness cast offer does not set the madness-paid flag") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bolt = driver.putCardInHand(player, "Madness Rider Bolt")
        discardViaSpell(driver, player, bolt)
        settle(driver)
        driver.submitYesNo(player, false)
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getGraveyard(player) shouldContain bolt
        ConditionEvaluator().evaluate(
            driver.state,
            MadnessCostWasPaid,
            EffectContext(sourceId = bolt, controllerId = player, wasMadness = false)
        ).shouldBeFalse()
    }
})
