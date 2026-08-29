package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Pollenbright Wings — {4}{G}{W} Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has flying.
 * Whenever enchanted creature deals combat damage to a player, create that many 1/1 green
 * Saproling creature tokens.
 */
class PollenbrightWingsScenarioTest : ScenarioTestBase() {

    init {
        context("Pollenbright Wings") {

            fun saprolingTokens(game: TestGame, playerId: EntityId): List<EntityId> =
                game.state.getZone(playerId, Zone.BATTLEFIELD).filter { id ->
                    game.state.getEntity(id)?.get<TokenComponent>() != null &&
                        game.state.projectedState.getSubtypes(id)
                            .any { it.equals("Saproling", ignoreCase = true) }
                }

            test("enchanted creature has flying") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Pollenbright Wings", "Grizzly Bears")
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("Grizzly Bears gains flying from the Aura") {
                    game.state.projectedState.hasKeyword(bears, Keyword.FLYING) shouldBe true
                }
            }

            test("combat damage creates that many Saproling tokens") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Pollenbright Wings", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers().error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                game.resolveStack()

                val tokens = saprolingTokens(game, game.player1Id)
                withClue("two combat damage creates two Saproling tokens") {
                    tokens.size shouldBe 2
                }
            }
        }
    }
}
