package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.isd.cards.DearlyDeparted
import com.wingedsheep.mtg.sets.definitions.isd.cards.VillageBellRinger
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Dearly Departed — while in your graveyard, Humans you control enter with an extra +1/+1.
 */
class DearlyDepartedScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(DearlyDeparted)
        driver.registerCard(VillageBellRinger)
        return driver
    }

    test("Humans enter with an extra +1/+1 while Dearly Departed is in your graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCardInGraveyard(you, "Dearly Departed")

        val card = driver.putCardInHand(you, "Village Bell-Ringer")
        driver.giveMana(you, Color.WHITE, 3)
        driver.castSpell(you, card).isSuccess shouldBe true
        driver.bothPass() // resolve creature
        while (driver.stackSize > 0) driver.bothPass()

        val human = driver.findPermanent(you, "Village Bell-Ringer")
        human.shouldNotBeNull()
        driver.state.getEntity(human)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
    }

    test("Humans do not get the bonus while Dearly Departed is on the battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(you, "Dearly Departed")

        val card = driver.putCardInHand(you, "Village Bell-Ringer")
        driver.giveMana(you, Color.WHITE, 3)
        driver.castSpell(you, card).isSuccess shouldBe true
        driver.bothPass()
        while (driver.stackSize > 0) driver.bothPass()

        val human = driver.findPermanent(you, "Village Bell-Ringer")
        human.shouldNotBeNull()
        val count = driver.state.getEntity(human)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
        count shouldBe 0
    }
})
