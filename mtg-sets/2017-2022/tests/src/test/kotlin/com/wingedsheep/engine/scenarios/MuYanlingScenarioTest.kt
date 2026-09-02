package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.SkipNextTurnComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Mu Yanling — Global Series: Jiang Yanggu & Mu Yanling #1
 */
class MuYanlingScenarioTest : ScenarioTestBase() {

    private fun loyalty(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    private fun seedLoyalty(game: TestGame, id: EntityId, amount: Int) {
        game.state = game.state.updateEntity(id) { c ->
            c.with(CountersComponent().withAdded(CounterType.LOYALTY, amount))
        }
    }

    private fun plusTwoAbility() =
        cardRegistry.getCard("Mu Yanling")!!.script.activatedAbilities[0]

    private fun minusThreeAbility() =
        cardRegistry.getCard("Mu Yanling")!!.script.activatedAbilities[1]

    private fun minusTenAbility() =
        cardRegistry.getCard("Mu Yanling")!!.script.activatedAbilities[2]

    init {
        test("+2 makes target creature unable to be blocked this turn") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Mu Yanling")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val yanling = game.findPermanent("Mu Yanling")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            seedLoyalty(game, yanling, 5)

            game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = yanling,
                    abilityId = plusTwoAbility().id,
                    targets = listOf(ChosenTarget.Permanent(bears)),
                ),
            ).error shouldBe null
            game.resolveStack()

            withClue("loyalty rises by two") {
                loyalty(game, yanling) shouldBe 7
            }
            withClue("the targeted creature can't be blocked this turn") {
                game.state.projectedState.hasKeyword(bears, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
            }
        }

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

        test("-10 taps all opponent creatures and grants an extra turn") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Mu Yanling")
                .withCardOnBattlefield(2, "Grizzly Bears", tapped = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val yanling = game.findPermanent("Mu Yanling")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            seedLoyalty(game, yanling, 10)

            game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = yanling,
                    abilityId = minusTenAbility().id,
                ),
            ).error shouldBe null
            game.resolveStack()

            withClue("loyalty drops by ten") {
                loyalty(game, yanling) shouldBe 0
            }
            withClue("opponent creatures are tapped") {
                game.state.getEntity(bears)?.has<TappedComponent>() shouldBe true
            }
            withClue("the caster takes an extra turn after this one") {
                game.state.getEntity(game.player2Id)?.has<SkipNextTurnComponent>() shouldBe true
            }
        }
    }
}
