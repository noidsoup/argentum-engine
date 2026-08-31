package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.gs1.cards.Mountain39
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Mountain — Global Series: Jiang Yanggu & Mu Yanling #39
 */
class Mountain39ScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(Mountain39.copy(setCode = "GS1"))

        test("taps for red") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Mountain", tapped = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val mountain = game.findPermanent("Mountain")!!
            val manaAbility = cardRegistry.getCard("Mountain")!!.activatedAbilities.first().id

            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = mountain, abilityId = manaAbility),
            ).error shouldBe null

            withClue("GS1 Mountain #39 taps for red") {
                game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.getAmount(Color.RED) shouldBe 1
                game.state.getEntity(mountain)?.has<TappedComponent>() shouldBe true
            }
        }
    }
}
