package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.gs1.cards.Forest40
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Forest — Global Series: Jiang Yanggu & Mu Yanling #40
 */
class Forest40ScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(Forest40.copy(setCode = "GS1"))

        test("taps for green") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Forest", tapped = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val forest = game.findPermanent("Forest")!!
            val manaAbility = cardRegistry.getCard("Forest")!!.activatedAbilities.first().id

            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = forest, abilityId = manaAbility),
            ).error shouldBe null

            withClue("GS1 Forest #40 taps for green") {
                game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.getAmount(Color.GREEN) shouldBe 1
                game.state.getEntity(forest)?.has<TappedComponent>() shouldBe true
            }
        }
    }
}
