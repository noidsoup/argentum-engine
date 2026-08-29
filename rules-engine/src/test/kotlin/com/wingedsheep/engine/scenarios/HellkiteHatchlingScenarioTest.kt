package com.wingedsheep.engine.scenarios

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
 * Hellkite Hatchling (CON #111 / PC2 #95) — Devour 1 and entry-timestamped flying/trample.
 */
class HellkiteHatchlingScenarioTest : ScenarioTestBase() {

    init {
        context("Hellkite Hatchling") {
            fun castWithDevourChoice(game: TestGame, sacrifice: List<String>) {
                game.castSpell(1, "Hellkite Hatchling").error shouldBe null
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

            test("devouring a creature grants flying and trample and a +1/+1 counter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Hellkite Hatchling")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castWithDevourChoice(game, listOf("Grizzly Bears"))

                val hatchling = game.findPermanent("Hellkite Hatchling")!!
                val projected = game.state.projectedState
                val counterCount = game.state.getEntity(hatchling)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                withClue("devour 1 places one +1/+1 counter") {
                    counterCount shouldBe 1
                }
                withClue("devoured hatchling has flying and trample") {
                    projected.hasKeyword(hatchling, Keyword.FLYING) shouldBe true
                    projected.hasKeyword(hatchling, Keyword.TRAMPLE) shouldBe true
                }
                withClue("sacrificed fodder is gone") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                }
            }

            test("declining devour leaves it without flying or trample") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Hellkite Hatchling")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castWithDevourChoice(game, emptyList())

                val hatchling = game.findPermanent("Hellkite Hatchling")!!
                val projected = game.state.projectedState
                val counterCount = game.state.getEntity(hatchling)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                withClue("no devour counters") {
                    counterCount shouldBe 0
                }
                withClue("no flying or trample without devouring") {
                    projected.hasKeyword(hatchling, Keyword.FLYING) shouldBe false
                    projected.hasKeyword(hatchling, Keyword.TRAMPLE) shouldBe false
                }
            }
        }
    }
}
