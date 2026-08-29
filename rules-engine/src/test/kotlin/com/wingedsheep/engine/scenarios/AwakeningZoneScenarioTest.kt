package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.roe.cards.AwakeningZone
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Awakening Zone (ROE #176) — {2}{G} Enchantment.
 *
 * At the beginning of your upkeep, you may create a 0/1 colorless Eldrazi Spawn creature token.
 */
class AwakeningZoneScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        cardRegistry.register(AwakeningZone)

        context("Awakening Zone") {

            fun eldraziSpawnTokens(game: TestGame, playerId: EntityId = game.player1Id): List<EntityId> =
                game.state.getZone(playerId, Zone.BATTLEFIELD).filter { id ->
                    game.state.getEntity(id)?.get<TokenComponent>() != null &&
                        game.state.projectedState.getSubtypes(id).any { it.equals("Eldrazi", ignoreCase = true) } &&
                        game.state.projectedState.getSubtypes(id).any { it.equals("Spawn", ignoreCase = true) }
                }

            test("upkeep may ability creates one 0/1 Eldrazi Spawn token") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Awakening Zone")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()

                withClue("optional upkeep trigger offers yes/no") {
                    game.hasPendingDecision() shouldBe true
                }
                game.answerYesNo(true)
                game.resolveStack()

                val tokens = eldraziSpawnTokens(game)
                withClue("one Eldrazi Spawn token is created") {
                    tokens.size shouldBe 1
                }
                withClue("the token is a 0/1 Eldrazi Spawn") {
                    projector.getProjectedPower(game.state, tokens.single()) shouldBe 0
                    projector.getProjectedToughness(game.state, tokens.single()) shouldBe 1
                }
            }
        }
    }
}
