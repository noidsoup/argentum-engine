package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Preyseizer Dragon (PC2 #50) — attack trigger deals damage equal to +1/+1 counters.
 */
class PreyseizerDragonScenarioTest : ScenarioTestBase() {

    init {
        context("Preyseizer Dragon") {
            test("attacking with two +1/+1 counters deals 2 damage to any target") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Preyseizer Dragon", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dragon = game.findPermanent("Preyseizer Dragon")!!
                game.state = game.state.updateEntity(dragon) { container ->
                    container.with(CountersComponent().withAdded(CounterType.PLUS_ONE_PLUS_ONE, 2))
                }

                val bears = game.findPermanent("Grizzly Bears")!!

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Preyseizer Dragon" to 2)).error shouldBe null

                if (game.state.pendingDecision == null) game.resolveStack()
                if (game.state.pendingDecision != null) {
                    game.selectTargets(listOf(bears)).error shouldBe null
                }
                game.resolveStack()

                withClue("two counters means 2 damage kills a 2/2 Grizzly Bears") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
            }

            test("devour 2 sacrifices one creature for two +1/+1 counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Preyseizer Dragon")
                    .withLandsOnBattlefield(1, "Mountain", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Preyseizer Dragon").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                while (game.state.stack.isNotEmpty() && game.getPendingDecision() == null) {
                    game.passPriority()
                }
                val devour = game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                val bears = game.findPermanent("Grizzly Bears")!!
                game.submitDecision(CardsSelectedResponse(devour.id, listOf(bears)))
                game.resolveStack()

                val dragon = game.findPermanent("Preyseizer Dragon")!!
                val counterCount = game.state.getEntity(dragon)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                withClue("devour 2 on one creature yields two counters") {
                    counterCount shouldBe 2
                }
            }
        }
    }
}
