package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.LibraryShuffledEvent
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
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

        test("shuffles the library once after both searches resolve") {
            val driver = GameTestDriver()
            driver.registerCards(TestCards.all)
            driver.initMirrorMatch(
                deck = Deck.of("Forest" to 40),
                skipMulligans = true,
                startingPlayer = 0,
            )
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val you = driver.player1
            val forestInLibrary = driver.state.getZone(ZoneKey(you, Zone.LIBRARY))
                .first { id -> driver.state.getEntity(id)?.get<CardComponent>()?.name == "Forest" }
            val yangguInLibrary = driver.putCardOnTopOfLibrary(you, "Jiang Yanggu")
            val journey = driver.putCardInHand(you, "Journey for the Elixir")

            driver.giveMana(you, Color.GREEN, 3)
            val shufflesBefore = driver.events.count { it is LibraryShuffledEvent && it.playerId == you }

            driver.submit(
                CastSpell(you, journey, paymentStrategy = PaymentStrategy.FromPool),
            ).error shouldBe null

            var guard = 0
            while (guard++ < 20) {
                when {
                    driver.isPaused -> {
                        val decision = driver.state.pendingDecision
                        if (decision is SelectCardsDecision) {
                            val pick = when {
                                decision.options.contains(forestInLibrary) -> forestInLibrary
                                decision.options.contains(yangguInLibrary) -> yangguInLibrary
                                else -> decision.options.first()
                            }
                            driver.submitCardSelection(you, listOf(pick))
                        } else {
                            driver.autoResolveDecision()
                        }
                    }
                    driver.state.stack.isNotEmpty() -> driver.bothPass()
                    else -> break
                }
            }

            withClue("oracle ends with a single shuffle after both cards are found") {
                driver.events.count { it is LibraryShuffledEvent && it.playerId == you } shouldBe
                    shufflesBefore + 1
            }
        }
    }
}
