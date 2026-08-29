package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.m10.cards.WallOfFrost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Wall of Frost — {1}{U}{U} Creature — Wall 0/7
 *
 * Defender
 * Whenever this creature blocks a creature, that creature doesn't untap during its controller's
 * next untap step.
 */
class WallOfFrostScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(WallOfFrost))
        return d
    }

    fun GameTestDriver.hasSkipNextUntap(creature: EntityId): Boolean =
        state.floatingEffects.any {
            creature in it.effect.affectedEntities &&
                it.duration is Duration.UntilAfterAffectedControllersNextUntap
        }

    test("blocking grants skip-next-untap to the blocked attacker") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Island" to 30), startingLife = 20)
        val attackerController = d.activePlayer!!
        val defender = d.getOpponent(attackerController)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val attacker = d.putCreatureOnBattlefield(attackerController, "Centaur Courser")
        d.removeSummoningSickness(attacker)
        val wall = d.putCreatureOnBattlefield(defender, "Wall of Frost")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(attackerController, listOf(attacker), defendingPlayer = defender).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(defender, mapOf(wall to listOf(attacker)))
        d.bothPass()

        d.hasSkipNextUntap(attacker) shouldBe true
    }
})
