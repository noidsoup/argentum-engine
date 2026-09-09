package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Hollowhenge Overlord (VOC #36) — Flash; at your upkeep, for each Wolf or Werewolf you control,
 * create a 2/2 green Wolf creature token.
 */
class HollowhengeOverlordScenarioTest : ScenarioTestBase() {

    init {
        context("Hollowhenge Overlord") {

            test("upkeep creates one Wolf token per Wolf or Werewolf you control, including itself") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hollowhenge Overlord", summoningSickness = false)
                    .withCardOnBattlefield(1, "Packsong Pup", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(2)
                    .inPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                    .build()

                val wolvesBefore = game.findPermanents("Wolf Token").size

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.state.activePlayerId shouldBe game.player1Id
                game.resolveStack()

                withClue("Overlord and Packsong Pup are two Wolves, so two tokens are created") {
                    game.findPermanents("Wolf Token").size shouldBe wolvesBefore + 2
                }
            }

            test("with no other Wolves, upkeep creates one token for Overlord alone") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hollowhenge Overlord", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(2)
                    .inPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                    .build()

                val wolvesBefore = game.findPermanents("Wolf Token").size

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.state.activePlayerId shouldBe game.player1Id
                game.resolveStack()

                withClue("only Overlord counts as a Wolf") {
                    game.findPermanents("Wolf Token").size shouldBe wolvesBefore + 1
                }
            }
        }
    }
}
