package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.handlers.DynamicAmountEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.state.components.stack.EntitySnapshot
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * [DynamicAmount.GreatestPowerSacrificedThisWay] — "gain life equal to the greatest power among
 * creatures sacrificed this way" after a greatest-power edict in the same resolving effect.
 *
 * Mirrors Shadowgrange Archfiend's ETB shape without authoring that card.
 */
class GreatestPowerSacrificedThisWayMechanicScenarioTest : FunSpec({

    val greatestPowerEdict = card("Greatest Power Edict Probe") {
        manaCost = "{4}{B}"
        typeLine = "Creature — Demon"
        power = 6
        toughness = 6
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = CompositeEffect(
                listOf(
                    Effects.Sacrifice(
                        GameObjectFilter.Creature.hasGreatestPower(),
                        target = EffectTarget.PlayerRef(Player.EachOpponent),
                    ),
                    Effects.GainLife(DynamicAmounts.greatestPowerSacrificedThisWay()),
                )
            )
        }
    }

    val big = card("Greatest Power Big") {
        manaCost = "{4}"
        typeLine = "Creature — Beast"
        power = 5
        toughness = 5
    }

    val small = card("Greatest Power Small") {
        manaCost = "{1}"
        typeLine = "Creature — Beast"
        power = 2
        toughness = 2
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(greatestPowerEdict, big, small))
        return driver
    }

    test("evaluator returns the max last-known power across all sacrificed snapshots") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        val evaluator = DynamicAmountEvaluator()
        val snapshots = listOf(
            EntitySnapshot(entityId = EntityId.generate(), power = 5),
            EntitySnapshot(entityId = EntityId.generate(), power = 2),
        )
        evaluator.evaluate(
            driver.state,
            DynamicAmounts.greatestPowerSacrificedThisWay(),
            EffectContext(
                sourceId = null,
                controllerId = driver.activePlayer!!,
                sacrificedPermanents = snapshots,
            ),
        ) shouldBe 5
    }

    test("evaluator ignores snapshots without power and returns zero when none were sacrificed") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        val evaluator = DynamicAmountEvaluator()
        evaluator.evaluate(
            driver.state,
            DynamicAmounts.greatestPowerSacrificedThisWay(),
            EffectContext(sourceId = null, controllerId = driver.activePlayer!!),
        ) shouldBe 0
        evaluator.evaluate(
            driver.state,
            DynamicAmounts.greatestPowerSacrificedThisWay(),
            EffectContext(
                sourceId = null,
                controllerId = driver.activePlayer!!,
                sacrificedPermanents = listOf(EntitySnapshot(entityId = EntityId.generate(), power = null)),
            ),
        ) shouldBe 0
    }

    test("life gain equals the greatest power among creatures sacrificed this way in a two-player game") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(opponent, "Greatest Power Big")
        driver.putCreatureOnBattlefield(opponent, "Greatest Power Small")

        val demon = driver.putCardInHand(you, "Greatest Power Edict Probe")
        driver.giveColorlessMana(you, 5)
        driver.submitSuccess(CastSpell(you, demon, paymentStrategy = PaymentStrategy.FromPool))
        driver.bothPass()

        val decision = driver.state.pendingDecision
        decision?.playerId shouldBe opponent
        driver.submitCardSelection(opponent, listOf(driver.findPermanent(opponent, "Greatest Power Big")!!))
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getLifeTotal(you) shouldBe 25
        driver.getLifeTotal(opponent) shouldBe 20
    }

    test("when no creature is sacrificed, life gain is zero") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val demon = driver.putCardInHand(you, "Greatest Power Edict Probe")
        driver.giveColorlessMana(you, 5)
        driver.submitSuccess(CastSpell(you, demon, paymentStrategy = PaymentStrategy.FromPool))
        driver.bothPass()
        while (driver.state.stack.isNotEmpty()) driver.bothPass()

        driver.getLifeTotal(you) shouldBe 20
    }
})
