package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.core.OrderObjectsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rtr.cards.NecropolisRegent
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Necropolis Regent (RTR #71) — whenever a creature you control deals combat damage to a player,
 * put that many +1/+1 counters on it.
 */
class NecropolisRegentScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(NecropolisRegent)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun GameTestDriver.swingUnblocked(player: EntityId, opponent: EntityId, attackers: List<EntityId>) {
        passPriorityUntil(Step.DECLARE_ATTACKERS)
        declareAttackers(player, attackers, opponent)
        passPriorityUntil(Step.DECLARE_BLOCKERS)
        passPriorityUntil(Step.COMBAT_DAMAGE)
        if (pendingDecision is CombatResolutionDecision) confirmCombatDamage()
        var guard = 0
        while (guard++ < 30) {
            val decision = pendingDecision
            when {
                decision is CombatResolutionDecision -> confirmCombatDamage()
                decision is OrderObjectsDecision ->
                    submitOrderedResponse(decision.playerId, decision.objects)
                decision != null -> autoResolveDecision()
                state.stack.isNotEmpty() -> bothPass()
                else -> return
            }
        }
    }

    test("combat damage to a player puts that many +1/+1 counters on the attacker") {
        val driver = newDriver()
        val you = driver.player1
        val opponent = driver.player2

        val regent = driver.putCreatureOnBattlefield(you, "Necropolis Regent")
        driver.removeSummoningSickness(regent)

        driver.swingUnblocked(you, opponent, listOf(regent))

        driver.plusOneCounters(regent) shouldBe 6
        driver.getLifeTotal(opponent) shouldBe 14
    }

    test("another creature you control that deals combat damage also grows") {
        val driver = newDriver()
        val you = driver.player1
        val opponent = driver.player2

        driver.putCreatureOnBattlefield(you, "Necropolis Regent")
        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        driver.removeSummoningSickness(bears)

        driver.swingUnblocked(you, opponent, listOf(bears))

        driver.plusOneCounters(bears) shouldBe 2
    }
})
