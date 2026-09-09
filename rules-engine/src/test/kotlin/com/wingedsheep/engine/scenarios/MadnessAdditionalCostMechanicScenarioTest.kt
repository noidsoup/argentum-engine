package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.identity.MadnessExiledComponent
import com.wingedsheep.engine.state.components.identity.PlayWithAdditionalCostComponent
import com.wingedsheep.engine.state.components.identity.PlayWithFixedAlternativeManaCostComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCost
import com.wingedsheep.sdk.scripting.costs.CostAtom
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Madness keyword with a bundled pay-life additional cost (CR 702.35b / 601.2f–h).
 *
 * Shadowgrange Archfiend prints "Madness—{2}{B}, Pay 8 life." The mana half replaces the printed
 * cost; the life payment is an additional cost that still applies on the madness cast path.
 */
class MadnessAdditionalCostMechanicScenarioTest : FunSpec({

    val payLifeMadness = card("Pay Life Madness Boon") {
        manaCost = "{6}{B}"
        typeLine = "Sorcery"
        spell { effect = Effects.GainLife(4) }
        madness("{2}{B}", AdditionalCost.Atom(CostAtom.PayLife(8)))
    }

    val selfDiscard = card("Pay Life Madness Discard") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        spell { effect = Effects.Discard(1) }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(payLifeMadness, selfDiscard))
        return driver
    }

    fun discardViaSpell(driver: GameTestDriver, player: EntityId, cardId: EntityId) {
        val spell = driver.putCardInHand(player, "Pay Life Madness Discard")
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

    test("discarding stamps madness fixed cost and bundled pay-life additional cost") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val boon = driver.putCardInHand(player, "Pay Life Madness Boon")
        discardViaSpell(driver, player, boon)

        val container = driver.state.getEntity(boon).shouldNotBeNull()
        container.get<MadnessExiledComponent>().shouldNotBeNull()
        container.get<PlayWithFixedAlternativeManaCostComponent>()?.fixedCost.toString() shouldBe "{2}{B}"
        container.get<PlayWithAdditionalCostComponent>()?.additionalCosts?.singleOrNull() shouldBe
            AdditionalCost.Atom(CostAtom.PayLife(8))
    }

    test("madness cast pays mana and life additional cost") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val boon = driver.putCardInHand(player, "Pay Life Madness Boon")
        discardViaSpell(driver, player, boon)

        driver.giveMana(player, Color.BLACK, 2)
        settle(driver)
        driver.submitYesNo(player, true)
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getLifeTotal(player) shouldBe 20 - 8 + 4
        driver.getGraveyard(player) shouldContain boon
        driver.getExile(player).shouldNotContain(boon)
        driver.state.getEntity(boon)?.get<PlayWithAdditionalCostComponent>() shouldBe null
    }

    test("madness cast is unaffordable when life is below the bundled pay-life cost") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 7)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val boon = driver.putCardInHand(player, "Pay Life Madness Boon")
        discardViaSpell(driver, player, boon)

        driver.giveMana(player, Color.BLACK, 2)
        settle(driver)
        driver.submitYesNo(player, true)
        while (driver.state.pendingDecision != null) driver.autoResolveDecision()
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getLifeTotal(player) shouldBe 7
        driver.getGraveyard(player) shouldContain boon
        driver.getExile(player).shouldNotContain(boon)
    }
})
