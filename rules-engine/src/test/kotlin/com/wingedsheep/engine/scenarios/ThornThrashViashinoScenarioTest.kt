package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Thorn-Thrash Viashino (ALA #116 / PC2 #52) — Devour 2 and a {G} trample pump.
 */
class ThornThrashViashinoScenarioTest : ScenarioTestBase() {

    private val trampleAbilityId =
        cardRegistry.getCard("Thorn-Thrash Viashino")!!.activatedAbilities.first().id

    init {
        context("Thorn-Thrash Viashino") {
            fun castWithDevourChoice(game: TestGame, sacrifice: List<String>) {
                game.castSpell(1, "Thorn-Thrash Viashino").error shouldBe null
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

            test("devouring one creature enters with two +1/+1 counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Thorn-Thrash Viashino")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castWithDevourChoice(game, listOf("Grizzly Bears"))

                val viashino = game.findPermanent("Thorn-Thrash Viashino")!!
                val counterCount = game.state.getEntity(viashino)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                withClue("devour 2 places two counters per sacrificed creature") {
                    counterCount shouldBe 2
                }
                withClue("sacrificed fodder is gone") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                }
            }

            test("{G} grants trample until end of turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Thorn-Thrash Viashino")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val viashino = game.findPermanent("Thorn-Thrash Viashino")!!
                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = viashino,
                        abilityId = trampleAbilityId,
                    ),
                )
                withClue("activating the pump should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                withClue("Thorn-Thrash Viashino gains trample") {
                    game.state.projectedState.hasKeyword(viashino, Keyword.TRAMPLE) shouldBe true
                }
            }
        }
    }
}
