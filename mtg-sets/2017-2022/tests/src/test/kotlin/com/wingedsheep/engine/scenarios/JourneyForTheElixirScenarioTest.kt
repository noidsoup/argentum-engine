package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Journey for the Elixir — Global Series: Jiang Yanggu & Mu Yanling #36
 * Search your library and graveyard for a basic land card and a card named Jiang Yanggu.
 */
class JourneyForTheElixirScenarioTest : ScenarioTestBase() {

    init {
        test("finds a basic land and Jiang Yanggu from the library") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Journey for the Elixir")
                .withCardInLibrary(1, "Forest#GS1-40")
                .withCardInLibrary(1, "Jiang Yanggu")
                .withLandsOnBattlefield(1, "Forest", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val forestInLibrary = game.state.getLibrary(game.player1Id).single { id ->
                game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
            }
            val yangguInLibrary = game.state.getLibrary(game.player1Id).single { id ->
                game.state.getEntity(id)?.get<CardComponent>()?.name == "Jiang Yanggu"
            }

            game.castSpell(1, "Journey for the Elixir").error shouldBe null
            game.resolveStack()

            withClue("search for the basic land") {
                game.hasPendingDecision() shouldBe true
            }
            game.selectCards(listOf(forestInLibrary))
            game.resolveStack()

            withClue("search for Jiang Yanggu") {
                game.hasPendingDecision() shouldBe true
            }
            game.selectCards(listOf(yangguInLibrary))
            game.resolveStack()

            withClue("both searched cards are in hand") {
                game.isInHand(1, "Forest") shouldBe true
                game.isInHand(1, "Jiang Yanggu") shouldBe true
            }
        }

        test("finds a basic land and Jiang Yanggu from the graveyard") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Journey for the Elixir")
                .withCardInGraveyard(1, "Forest#GS1-40")
                .withCardInGraveyard(1, "Jiang Yanggu")
                .withLandsOnBattlefield(1, "Forest", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val forestInGraveyard = game.state.getGraveyard(game.player1Id).single { id ->
                game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
            }
            val yangguInGraveyard = game.state.getGraveyard(game.player1Id).single { id ->
                game.state.getEntity(id)?.get<CardComponent>()?.name == "Jiang Yanggu"
            }

            game.castSpell(1, "Journey for the Elixir").error shouldBe null
            game.resolveStack()

            game.selectCards(listOf(forestInGraveyard))
            game.resolveStack()

            game.selectCards(listOf(yangguInGraveyard))
            game.resolveStack()

            withClue("both cards are tutored from the graveyard to hand") {
                game.isInHand(1, "Forest") shouldBe true
                game.isInHand(1, "Jiang Yanggu") shouldBe true
                game.isInGraveyard(1, "Forest") shouldBe false
                game.isInGraveyard(1, "Jiang Yanggu") shouldBe false
            }
        }
    }
}
