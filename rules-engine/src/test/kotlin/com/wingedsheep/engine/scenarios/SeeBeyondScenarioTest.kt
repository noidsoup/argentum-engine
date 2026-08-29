package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * See Beyond (ROE #86 / PC2 #25) — draw two, then shuffle one card from hand into library.
 */
class SeeBeyondScenarioTest : ScenarioTestBase() {

    init {
        context("See Beyond") {
            test("draws two cards then shuffles one chosen card into the library") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "See Beyond")
                    .withCardInHand(1, "Island")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(10) { builder = builder.withCardInLibrary(1, "Forest") }
                val game = builder.build()

                val handBefore = game.state.getZone(game.player1Id, Zone.HAND).size
                val libraryBefore = game.state.getZone(game.player1Id, Zone.LIBRARY).size

                game.castSpell(1, "See Beyond").error shouldBe null
                game.resolveStack()

                val drawDecision = game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                val bolt = game.findCardsInHand(1, "Lightning Bolt").first()
                game.submitDecision(CardsSelectedResponse(drawDecision.id, listOf(bolt)))
                game.resolveStack()

                val handAfter = game.state.getZone(game.player1Id, Zone.HAND).size
                val libraryAfter = game.state.getZone(game.player1Id, Zone.LIBRARY).size

                withClue("Lightning Bolt was shuffled into the library") {
                    game.findCardsInHand(1, "Lightning Bolt").isEmpty() shouldBe true
                }
                withClue("net hand size is unchanged (spell leaves hand, draw two, shuffle one)") {
                    handAfter shouldBe handBefore
                }
                withClue("library loses one card net (draw two, shuffle one back)") {
                    libraryAfter shouldBe libraryBefore - 1
                }
            }
        }
    }
}
