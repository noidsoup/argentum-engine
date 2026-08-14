package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.roe.cards.GuardGomazoa
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Guard Gomazoa — Prevent all combat damage that would be dealt to this creature.
 */
class GuardGomazoaScenarioTest : FunSpec({

    fun newGame(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(GuardGomazoa))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.markedDamage(id: EntityId): Int =
        state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    test("prevents combat damage dealt to Guard Gomazoa when it blocks") {
        val driver = newGame()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val attacker = driver.putCreatureOnBattlefield(you, "Centaur Courser") // 3/3
        driver.removeSummoningSickness(attacker)
        val gomazoa = driver.putCreatureOnBattlefield(opponent, "Guard Gomazoa") // 1/3

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, listOf(attacker), defendingPlayer = opponent).error shouldBe null
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(opponent, mapOf(gomazoa to listOf(attacker)))
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)

        driver.markedDamage(gomazoa) shouldBe 0
        driver.state.getBattlefield().contains(gomazoa) shouldBe true
    }
})
