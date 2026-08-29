package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Mycoloth (ALA #140 / PC2 #68) — Devour 2 and upkeep Saproling tokens equal to +1/+1 counters.
 */
class MycolothScenarioTest : ScenarioTestBase() {

    init {
        context("Mycoloth") {
            fun saprolingCount(game: TestGame, playerId: EntityId = game.player1Id): Int =
                game.state.getZone(playerId, Zone.BATTLEFIELD).count { id ->
                    game.state.getEntity(id)?.get<TokenComponent>() != null &&
                        game.state.projectedState.getSubtypes(id)
                            .any { it.equals("Saproling", ignoreCase = true) }
                }

            fun castWithDevourChoice(game: TestGame, sacrifice: List<String>) {
                game.castSpell(1, "Mycoloth").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                while (game.state.stack.isNotEmpty() && game.getPendingDecision() == null) {
                    game.passPriority()
                }
                val devour = game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                val ids = sacrifice.map { game.findPermanent(it)!! }
                game.submitDecision(CardsSelectedResponse(devour.id, ids))
                game.resolveStack()
            }

            test("devouring one creature places two +1/+1 counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Mycoloth")
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castWithDevourChoice(game, listOf("Grizzly Bears"))

                val mycoloth = game.findPermanent("Mycoloth")!!
                val counterCount = game.state.getEntity(mycoloth)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                withClue("devour 2 places two +1/+1 counters per sacrificed creature") {
                    counterCount shouldBe 2
                }
            }

            test("upkeep creates one Saproling token per +1/+1 counter") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Mycoloth")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val mycoloth = game.findPermanent("Mycoloth")!!
                game.state = game.state.updateEntity(mycoloth) { container ->
                    container.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 3)))
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("three +1/+1 counters make three Saproling tokens") {
                    saprolingCount(game) shouldBe 3
                }
            }
        }
    }
}
