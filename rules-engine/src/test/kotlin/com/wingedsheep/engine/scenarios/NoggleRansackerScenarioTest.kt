package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.eve.cards.NoggleRansacker
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Noggle Ransacker (EVE #109) — {2}{U/R} Creature — Noggle Rogue 2/1.
 *
 * When this creature enters, each player draws two cards, then discards a card at random.
 */
class NoggleRansackerScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(NoggleRansacker)

        context("Noggle Ransacker") {

            test("ETB makes each player draw two then discard one at random") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Noggle Ransacker")
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Swamp")
                    .withCardInLibrary(2, "Mountain")
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardInHand(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val p1HandBefore = game.handSize(1)
                val p2HandBefore = game.handSize(2)

                game.castSpell(1, "Noggle Ransacker").error shouldBe null
                repeat(20) {
                    when (val decision = game.getPendingDecision()) {
                        null -> if (game.state.stack.isEmpty()) return@repeat else game.resolveStack()
                        is com.wingedsheep.engine.core.SelectCardsDecision ->
                            game.selectCards(decision.options.take(decision.minSelections))
                        else -> game.resolveStack()
                    }
                }

                withClue("ransacker is on the battlefield") {
                    game.isOnBattlefield("Noggle Ransacker") shouldBe true
                }
                // Hand: -1 (cast) +2 (draw) -1 (random discard) = net 0 from start.
                withClue("player 1 net hand unchanged after draw-two discard-one") {
                    game.handSize(1) shouldBe p1HandBefore
                }
                withClue("player 2 drew two and discarded one (net +1 card)") {
                    game.handSize(2) shouldBe p2HandBefore + 1
                }
            }
        }
    }
}
