package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Ghostly Pilferer (M21 #52 / VOC #105) — untap optional draw, non-hand opponent casts, discard
 * for unblockable.
 */
class GhostlyPilfererScenarioTest : ScenarioTestBase() {

    init {
        context("Ghostly Pilferer") {

            test("draws when an opponent casts from anywhere other than their hand") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Ghostly Pilferer", summoningSickness = false)
                    .withCardInGraveyard(2, "Dreams of Laguna")
                    .withLandsOnBattlefield(2, "Island", 4)
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                val flashback = game.castSpellFromGraveyard(2, "Dreams of Laguna")
                withClue("flashback cast should succeed: ${flashback.error}") {
                    flashback.error shouldBe null
                }
                game.resolveStack()

                withClue("Ghostly Pilferer's non-hand cast trigger drew one card") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }

            test("does not draw when an opponent casts from their hand") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Ghostly Pilferer", summoningSickness = false)
                    .withCardInHand(2, "Opt")
                    .withLandsOnBattlefield(2, "Island", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                val cast = game.castSpell(2, "Opt")
                withClue("hand cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("a normal hand cast does not trigger Ghostly Pilferer") {
                    game.handSize(1) shouldBe handBefore
                }
            }

        }
    }
}
