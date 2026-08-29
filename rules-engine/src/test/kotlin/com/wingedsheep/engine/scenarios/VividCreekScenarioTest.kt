package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.lrw.cards.VividCreek
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Vivid Creek (LRW land).
 *
 * Enters tapped with two charge counters; taps for {U} or removes a charge counter for any color.
 */
class VividCreekScenarioTest : ScenarioTestBase() {

    init {
        val tapForBlueId = VividCreek.activatedAbilities[0].id
        val tapRemoveChargeId = VividCreek.activatedAbilities[1].id

        context("Vivid Creek") {
            test("enters tapped with two charge counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Vivid Creek")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val creekCard = game.state.getHand(game.player1Id).first {
                    game.state.getEntity(it)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Vivid Creek"
                }
                game.execute(PlayLand(game.player1Id, creekCard)).error shouldBe null

                val creek = game.findPermanent("Vivid Creek")!!
                withClue("enters tapped") {
                    game.state.getEntity(creek)?.has<TappedComponent>() shouldBe true
                }
                withClue("enters with two charge counters") {
                    val counters = game.state.getEntity(creek)?.get<CountersComponent>()
                    counters?.getCount(CounterType.CHARGE) shouldBe 2
                }
            }

            test("{T}: add {U}") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Vivid Creek", tapped = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val creek = game.findPermanent("Vivid Creek")!!
                game.state = game.state.updateEntity(creek) { container ->
                    val existing = container.get<CountersComponent>() ?: CountersComponent()
                    container.with(existing.withAdded(CounterType.CHARGE, 2))
                }
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = creek,
                        abilityId = tapForBlueId,
                    )
                ).error shouldBe null

                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                withClue("one blue mana added") {
                    pool?.blue shouldBe 1
                }
            }

            test("{T}, remove a charge counter: add one mana of any color") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Vivid Creek", tapped = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val creek = game.findPermanent("Vivid Creek")!!
                game.state = game.state.updateEntity(creek) { container ->
                    val existing = container.get<CountersComponent>() ?: CountersComponent()
                    container.with(existing.withAdded(CounterType.CHARGE, 2))
                }
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = creek,
                        abilityId = tapRemoveChargeId,
                        manaColorChoice = Color.RED,
                    )
                ).error shouldBe null

                val counters = game.state.getEntity(creek)?.get<CountersComponent>()
                val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
                withClue("spent one charge counter") {
                    counters?.getCount(CounterType.CHARGE) shouldBe 1
                }
                withClue("added one red mana") {
                    pool?.red shouldBe 1
                }
            }
        }
    }
}
