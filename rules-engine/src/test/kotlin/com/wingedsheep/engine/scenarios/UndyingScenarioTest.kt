package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for the undying keyword (CR 702.92).
 *
 * Undying: "When this creature dies, if it had no +1/+1 counters on it, return it to the
 * battlefield under its owner's control with a +1/+1 counter on it."
 *
 * Tests use [TestCards.UndyingTestCreature] (1/1) and Lightning Bolt as removal.
 */
class UndyingScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of(
                "Swamp" to 20,
                "Mountain" to 20
            ),
            skipMulligans = true
        )
        return driver
    }

    test("undying returns the creature with a +1/+1 counter when it dies with no +1/+1 counters") {
        val driver = createDriver()
        val caster = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(caster, "Undying Test Creature")
        val creature = driver.findPermanent(caster, "Undying Test Creature")
        creature.shouldNotBeNull()

        val bolt = driver.putCardInHand(caster, "Lightning Bolt")
        driver.giveMana(caster, Color.RED, 1)

        driver.castSpell(caster, bolt, listOf(creature)).isSuccess shouldBe true
        driver.bothPass() // resolve Lightning Bolt (3 damage → lethal)

        driver.stackSize shouldBeGreaterThanOrEqual 1

        driver.bothPass() // resolve undying trigger

        val returned = driver.findPermanent(caster, "Undying Test Creature")
        returned.shouldNotBeNull()

        val counters = driver.state.getEntity(returned)?.get<CountersComponent>()
        counters.shouldNotBeNull()
        counters.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
    }

    test("undying does not fire when the creature already has a +1/+1 counter at time of death") {
        val driver = createDriver()
        val caster = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(caster, "Undying Test Creature")
        val creature = driver.findPermanent(caster, "Undying Test Creature")
        creature.shouldNotBeNull()

        driver.replaceState(
            driver.state.updateEntity(creature) { c ->
                val counters = c.get<CountersComponent>() ?: CountersComponent()
                c.with(counters.withAdded(CounterType.PLUS_ONE_PLUS_ONE, 1))
            }
        )

        val bolt = driver.putCardInHand(caster, "Lightning Bolt")
        driver.giveMana(caster, Color.RED, 1)

        driver.castSpell(caster, bolt, listOf(creature)).isSuccess shouldBe true
        driver.bothPass() // resolve Lightning Bolt

        driver.findPermanent(caster, "Undying Test Creature") shouldBe null
        driver.getGraveyardCardNames(caster) shouldContain "Undying Test Creature"
    }

    test("a creature returned by undying cannot undying a second time (it now has a +1/+1 counter)") {
        val driver = createDriver()
        val caster = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(caster, "Undying Test Creature")
        val creature = driver.findPermanent(caster, "Undying Test Creature")
        creature.shouldNotBeNull()

        val bolt1 = driver.putCardInHand(caster, "Lightning Bolt")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, bolt1, listOf(creature)).isSuccess shouldBe true
        driver.bothPass() // resolve Lightning Bolt
        driver.bothPass() // resolve undying trigger

        val returned = driver.findPermanent(caster, "Undying Test Creature")
        returned.shouldNotBeNull()

        val bolt2 = driver.putCardInHand(caster, "Lightning Bolt")
        driver.giveMana(caster, Color.RED, 1)
        driver.castSpell(caster, bolt2, listOf(returned)).isSuccess shouldBe true
        driver.bothPass() // resolve Lightning Bolt

        driver.findPermanent(caster, "Undying Test Creature") shouldBe null
        driver.getGraveyardCardNames(caster).count { it == "Undying Test Creature" } shouldBe 1
    }

    test("undying does not fire on tokens (Rule 704.5d — tokens cease to exist)") {
        val driver = createDriver()
        val caster = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(caster, "Undying Test Creature")
        val tokenId = driver.findPermanent(caster, "Undying Test Creature")
        tokenId.shouldNotBeNull()
        driver.replaceState(
            driver.state.updateEntity(tokenId) { c -> c.with(TokenComponent) }
        )

        val bolt = driver.putCardInHand(caster, "Lightning Bolt")
        driver.giveMana(caster, Color.RED, 1)

        driver.castSpell(caster, bolt, listOf(tokenId)).isSuccess shouldBe true
        driver.bothPass() // resolve Lightning Bolt

        driver.findPermanent(caster, "Undying Test Creature") shouldBe null
        driver.state.getBattlefield().contains(tokenId) shouldBe false
    }
})
