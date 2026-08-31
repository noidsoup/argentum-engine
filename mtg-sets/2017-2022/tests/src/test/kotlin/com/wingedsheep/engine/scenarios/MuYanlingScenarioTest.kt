package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Mu Yanling — Global Series: Jiang Yanggu & Mu Yanling #1
 * −3: Draw two cards.
 */
class MuYanlingScenarioTest : ScenarioTestBase() {

    private fun loyalty(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    private fun seedLoyalty(game: TestGame, id: EntityId, amount: Int) {
        game.state = game.state.updateEntity(id) { c ->
            c.with(CountersComponent().withAdded(CounterType.LOYALTY, amount))
        }
    }

    private fun minusThreeAbility() =
        cardRegistry.getCard("Mu Yanling")!!.script.activatedAbilities[1]

    init {
        test("-3 draws two cards") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Mu Yanling")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val yanling = game.findPermanent("Mu Yanling")!!
            seedLoyalty(game, yanling, 5)
            val handBefore = game.handSize(1)

            game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = yanling,
                    abilityId = minusThreeAbility().id,
                ),
            ).error shouldBe null
            game.resolveStack()

            withClue("loyalty drops by three") {
                loyalty(game, yanling) shouldBe 2
            }
            withClue("the −3 ability draws two cards") {
                game.handSize(1) shouldBe handBefore + 2
            }
        }
    }
}
