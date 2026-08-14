package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.isd.cards.DiregrafGhoul
import com.wingedsheep.mtg.sets.definitions.isd.cards.UnbreathingHorde
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Unbreathing Horde — enters with counters from other Zombies / GY Zombies, and replaces
 * damage with removing a +1/+1 counter (PreventDamageAndRemoveCounter).
 */
class UnbreathingHordeScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(UnbreathingHorde)
        driver.registerCard(DiregrafGhoul)
        return driver
    }

    test("enters with a counter for each other Zombie you control and each Zombie in your graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), skipMulligans = true)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(you, "Diregraf Ghoul")
        driver.putCardInGraveyard(you, "Diregraf Ghoul")

        val hordeCard = driver.putCardInHand(you, "Unbreathing Horde")
        driver.giveMana(you, Color.BLACK, 3)
        driver.castSpell(you, hordeCard).isSuccess shouldBe true
        driver.bothPass()

        val horde = driver.findPermanent(you, "Unbreathing Horde")
        horde.shouldNotBeNull()
        val counters = driver.state.getEntity(horde)?.get<CountersComponent>()
        counters.shouldNotBeNull()
        // 1 other BF Zombie + 1 GY Zombie
        counters.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
    }

    test("damage is prevented and one +1/+1 counter is removed") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), skipMulligans = true)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(you, "Unbreathing Horde")
        val horde = driver.findPermanent(you, "Unbreathing Horde")!!
        driver.replaceState(
            driver.state.updateEntity(horde) { c ->
                val counters = c.get<CountersComponent>() ?: CountersComponent()
                c.with(counters.withAdded(CounterType.PLUS_ONE_PLUS_ONE, 2))
            },
        )

        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.giveMana(you, Color.RED, 1)
        driver.castSpell(you, bolt, listOf(horde)).isSuccess shouldBe true
        driver.bothPass()

        val after = driver.findPermanent(you, "Unbreathing Horde")
        after.shouldNotBeNull()
        driver.state.getEntity(after)?.get<DamageComponent>().shouldBeNull()
        driver.state.getEntity(after)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
    }

    test("without +1/+1 counters, damage is not prevented") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 20, "Mountain" to 20), skipMulligans = true)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(you, "Unbreathing Horde")
        val horde = driver.findPermanent(you, "Unbreathing Horde")!!

        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.giveMana(you, Color.RED, 1)
        driver.castSpell(you, bolt, listOf(horde)).isSuccess shouldBe true
        driver.bothPass()

        driver.findPermanent(you, "Unbreathing Horde") shouldBe null
    }
})
