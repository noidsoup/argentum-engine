package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.gs1.cards.Plains20
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Plains — Global Series: Jiang Yanggu & Mu Yanling #20
 */
class Plains20ScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(Plains20.copy(setCode = "GS1"))

        test("taps for white") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Plains", tapped = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val plains = game.findPermanent("Plains")!!
            val manaAbility = cardRegistry.getCard("Plains")!!.activatedAbilities.first().id

            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = plains, abilityId = manaAbility),
            ).error shouldBe null

            withClue("GS1 Plains #20 taps for white") {
                game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.getAmount(Color.WHITE) shouldBe 1
                game.state.getEntity(plains)?.has<TappedComponent>() shouldBe true
            }
        }
    }
}
