package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.matchers.shouldBe

/**
 * Jiang Yanggu — −1 creates Mowu when you don't already control one.
 */
class JiangYangguScenarioTest : ScenarioTestBase() {

    private fun loyalty(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    private fun seedLoyalty(game: TestGame, id: EntityId, amount: Int) {
        game.state = game.state.updateEntity(id) { c ->
            c.with(CountersComponent().withAdded(CounterType.LOYALTY, amount))
        }
    }

    private fun minusOneAbility() =
        cardRegistry.getCard("Jiang Yanggu")!!.script.activatedAbilities[1]

    private fun mowuOnBattlefield(game: TestGame): Boolean =
        game.state.getBattlefield(game.player1Id).any { id ->
            game.state.getEntity(id)?.get<CardComponent>()?.name == "Mowu"
        }

    init {
        test("-1 creates a legendary 3/3 green Dog named Mowu when none is on the battlefield") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Jiang Yanggu")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val yanggu = game.findPermanent("Jiang Yanggu")!!
            seedLoyalty(game, yanggu, 4)

            game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = yanggu,
                    abilityId = minusOneAbility().id,
                ),
            ).error shouldBe null
            game.resolveStack()

            loyalty(game, yanggu) shouldBe 3
            mowuOnBattlefield(game) shouldBe true
        }
    }
}
