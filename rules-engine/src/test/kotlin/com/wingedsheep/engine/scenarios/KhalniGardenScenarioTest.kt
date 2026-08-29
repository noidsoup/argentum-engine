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
import io.kotest.matchers.shouldNotBe

/**
 * Khalni Garden (WWK #138) — Land.
 *
 * This land enters tapped.
 * When this land enters, create a 0/1 green Plant creature token.
 * {T}: Add {G}.
 */
class KhalniGardenScenarioTest : ScenarioTestBase() {

    init {
        context("Khalni Garden") {

            test("enters tapped and creates a 0/1 green Plant token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Khalni Garden")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val garden = game.findCardsInHand(1, "Khalni Garden").first()
                game.execute(PlayLand(game.player1Id, garden)).error shouldBe null

                withClue("enters the battlefield tapped") {
                    game.state.getEntity(garden)?.has<TappedComponent>() shouldBe true
                }

                game.resolveStack()
                val plant = game.findPermanent("Plant Token") ?: game.findPermanent("Plant")
                withClue("ETB creates a 0/1 Plant") {
                    plant shouldNotBe null
                    game.state.projectedState.getPower(plant!!) shouldBe 0
                    game.state.projectedState.getToughness(plant) shouldBe 1
                }
            }

            test("{T}: Add {G}") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Khalni Garden", tapped = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val garden = game.findPermanent("Khalni Garden")!!
                val green = cardRegistry.getCard("Khalni Garden")!!.activatedAbilities.first().id
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = garden, abilityId = green)
                ).error shouldBe null

                withClue("taps for green") {
                    game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.green shouldBe 1
                    game.state.getEntity(garden)?.has<TappedComponent>() shouldBe true
                }
            }
        }
    }
}
