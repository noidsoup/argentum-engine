package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.model.Rarity
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Jiang Yanggu — −1 creates Mowu when you don't already control one.
 */
class JiangYangguScenarioTest : ScenarioTestBase() {

    private val mowu = card("Mowu") {
        manaCost = ""
        colorIdentity = "G"
        typeLine = "Legendary Creature — Dog"
        power = 3
        toughness = 3
        metadata {
            rarity = Rarity.SPECIAL
            collectorNumber = "T1"
        }
    }

    private fun loyalty(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    private fun seedLoyalty(game: TestGame, id: EntityId, amount: Int) {
        game.state = game.state.updateEntity(id) { c ->
            c.with(CountersComponent().withAdded(CounterType.LOYALTY, amount))
        }
    }

    private fun minusOneAbility() =
        cardRegistry.getCard("Jiang Yanggu")!!.script.activatedAbilities[1]

    private fun mowuCount(game: TestGame): Int =
        game.state.getBattlefield(game.player1Id).count { id ->
            game.state.getEntity(id)?.get<CardComponent>()?.name == "Mowu"
        }

    init {
        cardRegistry.register(mowu)

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
            mowuCount(game) shouldBe 1
        }

        test("-1 still pays loyalty but does not create a second Mowu when one is already controlled") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Jiang Yanggu")
                .withCardOnBattlefield(1, "Mowu", isToken = true)
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

            withClue("loyalty is spent even though Mowu was already on the battlefield") {
                loyalty(game, yanggu) shouldBe 3
            }
            withClue("the conditional token creation does not duplicate Mowu") {
                mowuCount(game) shouldBe 1
            }
        }
    }
}
