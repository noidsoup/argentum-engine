package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.gs1.cards.Island21
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Island — Global Series: Jiang Yanggu & Mu Yanling #21
 */
class Island21ScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(Island21.copy(setCode = "GS1"))

        test("taps for blue") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Island", tapped = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val island = game.findPermanent("Island")!!
            val manaAbility = cardRegistry.getCard("Island")!!.activatedAbilities.first().id

            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = island, abilityId = manaAbility),
            ).error shouldBe null

            withClue("GS1 Island #21 taps for blue") {
                game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.getAmount(Color.BLUE) shouldBe 1
                game.state.getEntity(island)?.has<TappedComponent>() shouldBe true
            }
        }
    }
}
