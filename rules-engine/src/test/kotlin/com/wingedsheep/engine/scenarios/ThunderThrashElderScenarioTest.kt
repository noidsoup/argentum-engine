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
 * Thunder-Thrash Elder (ALA #117 / PC2 #53) — Devour 3.
 */
class ThunderThrashElderScenarioTest : ScenarioTestBase() {

    init {
        context("Thunder-Thrash Elder") {
            fun castWithDevourChoice(game: TestGame, sacrifice: List<String>) {
                game.castSpell(1, "Thunder-Thrash Elder").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                while (game.state.stack.isNotEmpty() && game.getPendingDecision() == null) {
                    game.passPriority()
                }
                when (val devour = game.getPendingDecision()) {
                    is SelectCardsDecision -> {
                        val ids = sacrifice.map { game.findPermanent(it)!! }
                        game.submitDecision(CardsSelectedResponse(devour.id, ids))
                    }
                    null -> Unit
                    else -> devour.shouldBeInstanceOf<SelectCardsDecision>()
                }
                game.resolveStack()
            }

            test("devouring one creature enters with three +1/+1 counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Thunder-Thrash Elder")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castWithDevourChoice(game, listOf("Grizzly Bears"))

                val elder = game.findPermanent("Thunder-Thrash Elder")!!
                val counterCount = game.state.getEntity(elder)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                withClue("devour 3 places three counters per sacrificed creature") {
                    counterCount shouldBe 3
                }
                withClue("sacrificed fodder is gone") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                }
            }

            test("declining devour enters with no counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Thunder-Thrash Elder")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castWithDevourChoice(game, emptyList())

                val elder = game.findPermanent("Thunder-Thrash Elder")!!
                val counterCount = game.state.getEntity(elder)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                withClue("no sacrifices means no devour counters") {
                    counterCount shouldBe 0
                }
            }
        }
    }
}
