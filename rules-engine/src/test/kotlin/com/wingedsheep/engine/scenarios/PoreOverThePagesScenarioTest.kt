package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

class PoreOverThePagesScenarioTest : ScenarioTestBase() {
    init {
        test("draws three then discards one") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardInHand(1, "Pore Over the Pages")
                .withLandsOnBattlefield(1, "Island", 5)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(1, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val handBefore = game.state.getZone(game.player1Id, Zone.HAND).size
            game.castSpell(1, "Pore Over the Pages").error shouldBe null
            game.resolveStack()

            repeat(8) {
                when (val d = game.state.pendingDecision) {
                    is ChooseTargetsDecision -> game.selectTargets(emptyList())
                    is SelectCardsDecision -> game.selectCards(d.options.take(d.minSelections))
                    else -> return@repeat
                }
                if (game.state.stack.isNotEmpty()) game.resolveStack()
            }

            withClue("net +1 after cast/draw3/discard1 (Pore left hand)") {
                game.state.getZone(game.player1Id, Zone.HAND).size shouldBe handBefore + 1
            }
        }
    }
}
