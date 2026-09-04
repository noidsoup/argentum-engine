package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Tales of the Ancestors (KHC #8) — each player strictly behind the table's largest hand draws the
 * difference; players tied for most do not.
 */
class TalesOfTheAncestorsScenarioTest : ScenarioTestBase() {

    private fun TestGame.islandHandSize(playerNumber: Int): Int =
        findCardsInHand(playerNumber, "Island").size +
            findCardsInHand(playerNumber, "Tales of the Ancestors").size

    private fun board(myHand: Int, theirHand: Int): ScenarioBuilder {
        var builder = scenario()
            .withPlayers("Player1", "Player2")
            .withCardInHand(1, "Tales of the Ancestors")
            .withLandsOnBattlefield(1, "Island", 4)
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        repeat(myHand - 1) { builder = builder.withCardInHand(1, "Island") }
        repeat(theirHand) { builder = builder.withCardInHand(2, "Island") }
        repeat(10) {
            builder = builder.withCardInLibrary(1, "Island").withCardInLibrary(2, "Island")
        }
        return builder
    }

    init {
        context("Tales of the Ancestors") {
            test("the player behind the largest hand draws the difference") {
                val game = board(myHand = 2, theirHand = 5).build()

                game.castSpell(1, "Tales of the Ancestors").error shouldBe null
                game.resolveStack()

                withClue("player 1 had two cards to player 2's five, so player 1 draws three") {
                    game.islandHandSize(1) shouldBe 5
                    game.islandHandSize(2) shouldBe 5
                }
            }

            test("players tied for the most hand size draw nothing") {
                val game = board(myHand = 3, theirHand = 3).build()

                game.castSpell(1, "Tales of the Ancestors").error shouldBe null
                game.resolveStack()

                withClue("strictly fewer than the most — a tie at the top does not qualify") {
                    game.islandHandSize(1) shouldBe 3
                    game.islandHandSize(2) shouldBe 3
                }
            }
        }
    }
}
