package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.dka.cards.HavengulRunebinder
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class HavengulRunebinderScenarioTest : ScenarioTestBase() {
    init {
        val abilityId = HavengulRunebinder.activatedAbilities.first().id

        test("creates a Zombie and pumps Zombies you control") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Havengul Runebinder", summoningSickness = false)
                .withCardOnBattlefield(1, "Diregraf Ghoul")
                .withCardInGraveyard(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val runebinder = game.findPermanent("Havengul Runebinder")!!
            val bfBefore = game.state.getZone(game.player1Id, Zone.BATTLEFIELD).size
            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = runebinder,
                    abilityId = abilityId,
                ),
            )
            withClue("${result.error}") { result.error shouldBe null }

            repeat(4) {
                when (val d = game.state.pendingDecision) {
                    is SelectCardsDecision -> game.selectCards(d.options.take(d.minSelections))
                    else -> return@repeat
                }
            }
            if (game.state.stack.isNotEmpty()) game.resolveStack()

            game.state.getZone(game.player1Id, Zone.BATTLEFIELD).size shouldBeGreaterThanOrEqual bfBefore + 1
            game.state.getZone(game.player1Id, Zone.GRAVEYARD).isEmpty() shouldBe true
            val ghoul = game.findPermanent("Diregraf Ghoul")
            ghoul shouldNotBe null
            val counters = game.state.getEntity(ghoul!!)
                ?.get<CountersComponent>()
                ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
            counters shouldBe 1
        }
    }
}
