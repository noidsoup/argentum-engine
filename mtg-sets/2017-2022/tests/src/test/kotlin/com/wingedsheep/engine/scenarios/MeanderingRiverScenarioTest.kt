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

/** Meandering River — GS1 reprint · enters tapped, taps for W or U */
class MeanderingRiverScenarioTest : ScenarioTestBase() {

    init {
        context("Meandering River — enters tapped") {

            test("enters tapped when played") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Meandering River")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val river = game.findCardsInHand(1, "Meandering River").first()
                game.execute(PlayLand(game.player1Id, river)).error shouldBe null

                withClue("Meandering River always enters tapped") {
                    game.state.getEntity(river)?.has<TappedComponent>() shouldBe true
                }
            }

            test("{T}: Add {W}") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Meandering River", tapped = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val river = game.findPermanent("Meandering River")!!
                val whiteAbility = cardRegistry.getCard("Meandering River")!!.activatedAbilities[0].id

                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = river, abilityId = whiteAbility)
                ).error shouldBe null

                withClue("taps for white") {
                    game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.white shouldBe 1
                }
            }
        }
    }
}
