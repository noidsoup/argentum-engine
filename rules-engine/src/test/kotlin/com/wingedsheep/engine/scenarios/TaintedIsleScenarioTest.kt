package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.tor.cards.TaintedIsle
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tainted Isle — conditional colored mana when you control a Swamp.
 */
class TaintedIsleScenarioTest : ScenarioTestBase() {

    init {
        val tapColorlessId = TaintedIsle.activatedAbilities[0].id
        val tapBlueBlackId = TaintedIsle.activatedAbilities[1].id

        context("Tainted Isle") {
            test("can add colorless mana without a Swamp") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Tainted Isle", tapped = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val isle = game.findPermanent("Tainted Isle")!!
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = isle,
                        abilityId = tapColorlessId,
                    ),
                ).error shouldBe null

                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                withClue("one colorless mana added") {
                    pool?.colorless shouldBe 1
                }
            }

            test("cannot add blue or black without a Swamp") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Tainted Isle", tapped = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val isle = game.findPermanent("Tainted Isle")!!
                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = isle,
                        abilityId = tapBlueBlackId,
                        manaColorChoice = Color.BLUE,
                    ),
                )
                withClue("colored mana ability is illegal without a Swamp") {
                    result.error shouldNotBe null
                }
            }

            test("can add blue or black when you control a Swamp") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Tainted Isle", tapped = false)
                    .withCardOnBattlefield(1, "Swamp", tapped = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val isle = game.findPermanent("Tainted Isle")!!
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = isle,
                        abilityId = tapBlueBlackId,
                        manaColorChoice = Color.BLUE,
                    ),
                ).error shouldBe null

                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                withClue("one blue mana added") {
                    pool?.blue shouldBe 1
                }
            }
        }
    }
}
