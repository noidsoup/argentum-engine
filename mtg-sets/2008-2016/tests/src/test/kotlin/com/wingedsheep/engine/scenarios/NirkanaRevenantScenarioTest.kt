package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.roe.cards.NirkanaRevenant
import com.wingedsheep.sdk.core.Color
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Nirkana Revenant (ROE #120) — Swamp tap bonus mana and {B} pump.
 */
class NirkanaRevenantScenarioTest : ScenarioTestBase() {

    init {
        context("Nirkana Revenant") {

            test("tapping a Swamp adds an additional black mana") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Nirkana Revenant")
                    .withCardOnBattlefield(1, "Swamp")
                    .withActivePlayer(1)
                    .build()

                val swamp = game.findPermanent("Swamp")!!
                val abilityId = cardRegistry.getCard("Swamp")!!.activatedAbilities[0].id

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = swamp,
                        abilityId = abilityId,
                    ),
                )
                withClue("Swamp mana ability should activate: ${activation.error}") {
                    activation.error shouldBe null
                }

                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                withClue("Swamp produces {B} plus Nirkana's bonus {B}") {
                    pool?.getAmount(Color.BLACK) shouldBe 2
                }
            }

            test("{B} pumps this creature until end of turn") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Nirkana Revenant")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withActivePlayer(1)
                    .build()

                val revenant = game.findPermanent("Nirkana Revenant")!!
                val pumpId = NirkanaRevenant.activatedAbilities[0].id

                withClue("precondition: 4/4") {
                    game.state.projectedState.getPower(revenant) shouldBe 4
                    game.state.projectedState.getToughness(revenant) shouldBe 4
                }

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = revenant,
                        abilityId = pumpId,
                    ),
                )
                withClue("pump ability should activate: ${activation.error}") {
                    activation.error shouldBe null
                }
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("Nirkana Revenant is 5/5 until end of turn") {
                    game.state.projectedState.getPower(revenant) shouldBe 5
                    game.state.projectedState.getToughness(revenant) shouldBe 5
                }
            }
        }
    }
}
