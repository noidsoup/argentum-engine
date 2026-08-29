package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Farsight Mask — {5} Artifact
 *
 * Whenever a source an opponent controls deals damage to you, if this artifact is untapped,
 * you may draw a card.
 */
class FarsightMaskScenarioTest : ScenarioTestBase() {

    init {
        context("Farsight Mask") {

            fun advanceToDecision(game: TestGame) {
                var guard = 0
                while (!game.hasPendingDecision() && guard++ < 20) {
                    if (game.state.priorityPlayerId == null) break
                    game.passPriority()
                }
            }

            test("untapped mask offers a draw when an opponent's spell deals damage to you") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Farsight Mask")
                    .withCardInHand(2, "Lightning Bolt")
                    .withCardInLibrary(1, "Island")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                val libraryBefore = game.librarySize(1)

                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
                game.resolveStack()
                advanceToDecision(game)

                withClue("the may-draw trigger should raise a yes/no decision") {
                    (game.getPendingDecision() is YesNoDecision) shouldBe true
                }
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()

                withClue("accepting draws exactly one card") {
                    game.handSize(1) shouldBe handBefore + 1
                    game.librarySize(1) shouldBe libraryBefore - 1
                }
            }

            test("tapped mask does not offer a draw when you take damage") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Farsight Mask", tapped = true)
                    .withCardInHand(2, "Lightning Bolt")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                val libraryBefore = game.librarySize(1)

                game.castSpellTargetingPlayer(2, "Lightning Bolt", 1).error shouldBe null
                game.resolveStack()
                advanceToDecision(game)

                withClue("no draw decision while the mask is tapped") {
                    game.hasPendingDecision() shouldBe false
                }
                withClue("hand and library are unchanged") {
                    game.handSize(1) shouldBe handBefore
                    game.librarySize(1) shouldBe libraryBefore
                }
            }
        }
    }
}
