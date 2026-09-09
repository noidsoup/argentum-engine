package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseNumberDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.DistributeDecision
import com.wingedsheep.engine.core.DistributionResponse
import com.wingedsheep.engine.core.NumberChosenResponse
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.soi.cards.AvacynsJudgment
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Avacyn's Judgment — normal cast divides 2 damage; madness cast divides X damage among
 * permanents and/or players instead.
 */
class AvacynsJudgmentScenarioTest : FunSpec({

    val selfDiscard = card("Judgment Discard Outlet") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        spell { effect = Effects.Discard(1) }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(AvacynsJudgment, selfDiscard))
        return driver
    }

    fun discardViaSpell(driver: GameTestDriver, player: EntityId, cardId: EntityId) {
        val spell = driver.putCardInHand(player, "Judgment Discard Outlet")
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

    fun resolveDividedDamage(
        driver: GameTestDriver,
        chooser: EntityId,
        planner: (DistributeDecision) -> Map<EntityId, Int>,
    ) {
        repeat(12) {
            val decision = driver.state.pendingDecision
            if (decision is DistributeDecision) {
                driver.submitDecision(chooser, DistributionResponse(decision.id, planner(decision)))
            } else if (driver.stackSize > 0 || driver.state.priorityPlayerId != null) {
                driver.bothPass()
            }
        }
    }

    test("casting from hand divides 2 damage among any targets") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val judgment = driver.putCardInHand(caster, "Avacyn's Judgment")
        driver.giveMana(caster, Color.RED, 2)
        driver.castSpellWithTargets(caster, judgment, listOf(ChosenTarget.Player(opponent))).isSuccess shouldBe true

        resolveDividedDamage(driver, caster) { decision ->
            mapOf(decision.targets.first() to decision.totalAmount)
        }

        driver.getLifeTotal(opponent) shouldBe 18
    }

    test("madness cast divides X damage among permanents and/or players") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // A battlefield permanent proves the madness branch (X damage) fired instead of the normal
        // 2-damage mode, which would have changed this creature's toughness.
        val bear = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val judgment = driver.putCardInHand(caster, "Avacyn's Judgment")
        discardViaSpell(driver, caster, judgment)

        driver.giveMana(caster, Color.RED, 4)
        settle(driver)
        driver.submitYesNo(caster, true)

        var guard = 0
        while (guard++ < 40 && driver.state.pendingDecision != null) {
            when (val decision = driver.state.pendingDecision) {
                is ChooseTargetsDecision ->
                    driver.submitDecision(
                        caster,
                        TargetsResponse(decision.id, mapOf(0 to listOf(opponent))),
                    )
                is ChooseNumberDecision ->
                    driver.submitDecision(caster, NumberChosenResponse(decision.id, 3))
                is SelectManaSourcesDecision ->
                    driver.submitManaAutoPayOrDecline(caster, autoPay = true)
                else -> driver.bothPass()
            }
        }

        while (driver.stackSize > 0 && driver.state.pendingDecision == null && guard++ < 20) {
            driver.bothPass()
        }

        driver.getLifeTotal(opponent) shouldBe 17
        driver.state.projectedState.getToughness(bear) shouldBe 2
    }
})
