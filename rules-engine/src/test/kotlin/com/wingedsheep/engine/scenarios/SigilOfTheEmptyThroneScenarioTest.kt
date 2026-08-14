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
 * Scenario tests for Sigil of the Empty Throne (CON #18) — {3}{W}{W} Enchantment.
 *
 * "Whenever you cast an enchantment spell, create a 4/4 white Angel creature token with flying."
 *
 * Covers the cast trigger, the token's stats/type/flying, and that casting Sigil itself doesn't
 * trigger it — it isn't on the battlefield yet (ruling 2021-03-19).
 */
class SigilOfTheEmptyThroneScenarioTest : ScenarioTestBase() {

    init {
        context("Sigil of the Empty Throne") {

            fun tokensControlledBy(game: TestGame, playerId: EntityId): List<EntityId> =
                game.state.getZone(playerId, Zone.BATTLEFIELD).filter { id ->
                    game.state.getEntity(id)?.get<TokenComponent>() != null
                }

            test("casting an enchantment creates a 4/4 white flying Angel") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Sigil of the Empty Throne")
                    .withCardInHand(1, "Test Enchantment")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Test Enchantment")
                withClue("Casting the enchantment should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val tokens = tokensControlledBy(game, game.player1Id)
                withClue("exactly one token was created") { tokens.size shouldBe 1 }

                val projected = game.state.projectedState
                val angel = tokens.first()
                withClue("the token is a 4/4") {
                    projected.getPower(angel) shouldBe 4
                    projected.getToughness(angel) shouldBe 4
                }
                withClue("the token is an Angel") {
                    projected.getSubtypes(angel).any { it.equals("Angel", ignoreCase = true) } shouldBe true
                }
                withClue("the token has flying") {
                    projected.hasKeyword(angel, Keyword.FLYING) shouldBe true
                }
            }

            test("casting Sigil itself does not trigger it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sigil of the Empty Throne")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Sigil of the Empty Throne")
                withClue("Casting Sigil should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("Sigil resolved onto the battlefield") {
                    game.isOnBattlefield("Sigil of the Empty Throne") shouldBe true
                }
                withClue("no token — Sigil wasn't on the battlefield while it was being cast") {
                    tokensControlledBy(game, game.player1Id).size shouldBe 0
                }
            }

            test("an opponent casting an enchantment does not trigger it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Sigil of the Empty Throne")
                    .withCardInHand(2, "Test Enchantment")
                    .withLandsOnBattlefield(2, "Plains", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "Test Enchantment").error shouldBe null
                game.resolveStack()

                withClue("the trigger is 'whenever YOU cast an enchantment spell'") {
                    tokensControlledBy(game, game.player1Id).size shouldBe 0
                    tokensControlledBy(game, game.player2Id).size shouldBe 0
                }
            }
        }
    }
}
