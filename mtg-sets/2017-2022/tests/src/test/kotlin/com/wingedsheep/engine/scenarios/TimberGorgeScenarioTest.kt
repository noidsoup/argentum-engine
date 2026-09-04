package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Timber Gorge — GS1 reprint · enters tapped, taps for R or G */
class TimberGorgeScenarioTest : ScenarioTestBase() {

    init {
        context("Timber Gorge — enters tapped") {

            test("enters tapped when played") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Timber Gorge")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val gorge = game.findCardsInHand(1, "Timber Gorge").first()
                game.execute(PlayLand(game.player1Id, gorge)).error shouldBe null

                withClue("Timber Gorge always enters tapped") {
                    game.state.getEntity(gorge)?.has<TappedComponent>() shouldBe true
                }
            }

            test("{T}: Add {R}") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Timber Gorge", tapped = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val gorge = game.findPermanent("Timber Gorge")!!
                val redAbility = cardRegistry.getCard("Timber Gorge")!!.activatedAbilities[0].id

                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = gorge, abilityId = redAbility)
                ).error shouldBe null

                withClue("taps for red") {
                    game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.red shouldBe 1
                }
            }
        }
    }
}
