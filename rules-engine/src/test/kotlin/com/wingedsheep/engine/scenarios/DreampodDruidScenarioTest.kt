package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Dreampod Druid — At the beginning of each upkeep, if this creature is enchanted, create a 1/1
 * green Saproling creature token.
 */
class DreampodDruidScenarioTest : ScenarioTestBase() {

    init {
        context("Dreampod Druid") {

            fun saprolingCount(game: TestGame, playerId: EntityId = game.player1Id): Int =
                game.state.getZone(playerId, Zone.BATTLEFIELD).count { id ->
                    game.state.getEntity(id)?.get<TokenComponent>() != null &&
                        game.state.projectedState.getSubtypes(id)
                            .any { it.equals("Saproling", ignoreCase = true) }
                }

            test("upkeep with an Aura creates a Saproling token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Dreampod Druid")
                    .withCardAttachedTo(1, "Rancor", "Dreampod Druid")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                withClue("enchanted Dreampod Druid triggers on upkeep") {
                    game.state.stack.isNotEmpty() shouldBe true
                }
                game.resolveStack()

                withClue("enchanted Dreampod Druid makes a Saproling on upkeep") {
                    saprolingCount(game) shouldBe 1
                }
            }

            test("upkeep without an Aura does not create a token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Dreampod Druid")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)

                withClue("unenchanted Dreampod Druid is silent on upkeep") {
                    game.state.stack.isEmpty() shouldBe true
                    saprolingCount(game) shouldBe 0
                }
            }
        }
    }
}
