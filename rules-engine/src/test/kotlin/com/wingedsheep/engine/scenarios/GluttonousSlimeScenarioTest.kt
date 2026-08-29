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
 * Gluttonous Slime (CON #83 / PC2 #65) — Flash and Devour 1.
 */
class GluttonousSlimeScenarioTest : ScenarioTestBase() {

    init {
        context("Gluttonous Slime") {
            fun castWithDevourChoice(game: TestGame, sacrifice: List<String>) {
                game.castSpell(1, "Gluttonous Slime").error shouldBe null
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

            test("devouring a creature places a +1/+1 counter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Gluttonous Slime")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castWithDevourChoice(game, listOf("Grizzly Bears"))

                val slime = game.findPermanent("Gluttonous Slime")!!
                val counterCount = game.state.getEntity(slime)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                withClue("devour 1 places one +1/+1 counter") {
                    counterCount shouldBe 1
                }
                withClue("sacrificed fodder is gone") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                }
            }

            test("declining devour enters with no counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Gluttonous Slime")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castWithDevourChoice(game, emptyList())

                val slime = game.findPermanent("Gluttonous Slime")!!
                val counterCount = game.state.getEntity(slime)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                withClue("no devour counters when declined") {
                    counterCount shouldBe 0
                }
            }
        }
    }
}
