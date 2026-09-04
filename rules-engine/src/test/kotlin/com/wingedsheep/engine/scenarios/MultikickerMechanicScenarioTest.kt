package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.state.components.battlefield.kickerTimesChoice
import com.wingedsheep.engine.state.components.battlefield.wasKickedChoice
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.ChoiceSlot
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.CastChoiceMade
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Multikicker (CR 702.33) — the number of times the optional cost was paid must be readable as a
 * [DynamicAmount] via [DynamicAmounts.kickerTimes] / [ChoiceSlot.KICKED], not just a boolean
 * kicked flag. Marshal's Anthem: "return up to X … where X is the number of times it was kicked."
 */
class MultikickerMechanicScenarioTest : ScenarioTestBase() {

    private val testMultikickerCreature = card("Test Multikicker Creature") {
        manaCost = "{2}{G}"
        typeLine = "Creature — Elf"
        power = 1
        toughness = 1
        keywordAbility(KeywordAbility.multikicker("{1}"))
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.CreateToken(
                count = DynamicAmounts.kickerTimes(),
                power = 1,
                toughness = 1,
                creatureTypes = setOf("Soldier"),
            )
        }
    }

    private val testSingleKickerSorcery = card("Test Single Kicker Sorcery") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        keywordAbility(KeywordAbility.kicker("{1}"))
        spell {
            effect = Effects.CreateToken(
                count = DynamicAmounts.kickerTimes(),
                power = 1,
                toughness = 1,
                creatureTypes = setOf("Spirit"),
            )
        }
    }

    init {
        cardRegistry.register(listOf(testMultikickerCreature, testSingleKickerSorcery))

        test("not kicked — kickerTimes is 0 and no bonus tokens are created") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Test Multikicker Creature")
                .withLandsOnBattlefield(1, "Forest", 4)
                .build()

            game.castSpell(1, "Test Multikicker Creature").error shouldBe null
            game.resolveStack()

            game.findPermanents("Soldier Token").size shouldBe 0
            val elfId = game.findPermanents("Test Multikicker Creature").single()
            game.state.getEntity(elfId)?.kickerTimesChoice() shouldBe 0
            game.state.getEntity(elfId)?.wasKickedChoice() shouldBe false
        }

        test("multikicker paid 3 times creates 3 Soldier tokens and stores count durably") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Test Multikicker Creature")
                .withLandsOnBattlefield(1, "Forest", 6)
                .build()

            val cardId = game.findCardsInHand(1, "Test Multikicker Creature").single()
            game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = cardId,
                    declaredCostSlot = ChoiceSlot.KICKED,
                    optionalCostTimes = 3,
                    paymentStrategy = PaymentStrategy.AutoPay,
                )
            ).error shouldBe null
            game.resolveStack()

            game.findPermanents("Soldier Token").size shouldBe 3
            val elfId = game.findPermanents("Test Multikicker Creature").single()
            game.state.getEntity(elfId)?.kickerTimesChoice() shouldBe 3
            game.state.getEntity(elfId)?.wasKickedChoice() shouldBe true
        }

        test("single kicker payment stores count 1 and WasKicked remains true") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Test Multikicker Creature")
                .withLandsOnBattlefield(1, "Forest", 4)
                .build()

            val cardId = game.findCardsInHand(1, "Test Multikicker Creature").single()
            game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = cardId,
                    declaredCostSlot = ChoiceSlot.KICKED,
                    optionalCostTimes = 1,
                    paymentStrategy = PaymentStrategy.AutoPay,
                )
            ).error shouldBe null
            game.resolveStack()

            game.findPermanents("Soldier Token").size shouldBe 1
            val elfId = game.findPermanents("Test Multikicker Creature").single()
            game.state.getEntity(elfId)?.kickerTimesChoice() shouldBe 1

            val evaluator = ConditionEvaluator()
            val ctx = EffectContext(sourceId = elfId, controllerId = game.player1Id)
            evaluator.evaluate(game.state, WasKicked, ctx) shouldBe true
            evaluator.evaluate(game.state, CastChoiceMade(ChoiceSlot.KICKED), ctx) shouldBe true
        }

        test("kicked sorcery reads kickerTimes from stack context at resolution") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Test Single Kicker Sorcery")
                .withLandsOnBattlefield(1, "Island", 2)
                .build()

            val cardId = game.findCardsInHand(1, "Test Single Kicker Sorcery").single()
            game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = cardId,
                    declaredCostSlot = ChoiceSlot.KICKED,
                    paymentStrategy = PaymentStrategy.AutoPay,
                )
            ).error shouldBe null
            game.resolveStack()

            game.findPermanents("Spirit Token").size shouldBe 1
        }

        test("reject multikicker times on a card without multikicker") {
            val driver = GameTestDriver()
            driver.registerCards(TestCards.all)
            driver.registerCard(testSingleKickerSorcery)

            driver.initMirrorMatch(deck = Deck.of("Island" to 30, "Test Single Kicker Sorcery" to 4))
            val p1 = driver.player1
            val cardId = driver.putCardInHand(p1, "Test Single Kicker Sorcery")
            repeat(4) { driver.putLandOnBattlefield(p1, "Island") }
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val result = driver.submit(
                CastSpell(
                    playerId = p1,
                    cardId = cardId,
                    declaredCostSlot = ChoiceSlot.KICKED,
                    optionalCostTimes = 2,
                    paymentStrategy = PaymentStrategy.AutoPay,
                )
            )
            result.isSuccess shouldBe false
            result.error shouldBe "This spell does not have multikicker"
        }

        test("reject kicked cast with zero times paid") {
            val driver = GameTestDriver()
            driver.registerCards(TestCards.all)
            driver.registerCard(testMultikickerCreature)

            driver.initMirrorMatch(deck = Deck.of("Forest" to 30, "Test Multikicker Creature" to 4))
            val p1 = driver.player1
            val cardId = driver.putCardInHand(p1, "Test Multikicker Creature")
            repeat(3) { driver.putLandOnBattlefield(p1, "Forest") }
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val result = driver.submit(
                CastSpell(
                    playerId = p1,
                    cardId = cardId,
                    declaredCostSlot = ChoiceSlot.KICKED,
                    optionalCostTimes = 0,
                    paymentStrategy = PaymentStrategy.AutoPay,
                )
            )
            result.isSuccess shouldBe false
            result.error shouldBe "Optional additional cost must be paid at least once"
        }
    }
}
