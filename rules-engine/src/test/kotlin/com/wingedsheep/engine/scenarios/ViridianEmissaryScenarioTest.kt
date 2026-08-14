package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Viridian Emissary (MBS #95) — {1}{G} 2/1 Phyrexian Elf Scout.
 *
 * "When this creature dies, you may search your library for a basic land card, put it onto the
 *  battlefield tapped, then shuffle."
 *
 * The load-bearing details are that the fetched land arrives *on the battlefield tapped* (not in
 * hand, not untapped), that only basic lands are findable, and that the "you may" can be declined.
 */
class ViridianEmissaryScenarioTest : ScenarioTestBase() {

    init {
        context("Viridian Emissary") {

            fun board() = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Viridian Emissary")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

            /** Bolt our own 2/1 to put the dies-trigger on the stack. */
            fun killTheEmissary(game: TestGame) {
                val emissary = game.findPermanent("Viridian Emissary")!!
                game.castSpell(1, "Lightning Bolt", emissary).error shouldBe null
                game.resolveStack()
                game.isOnBattlefield("Viridian Emissary") shouldBe false
                var guard = 0
                while (!game.hasPendingDecision() && guard++ < 10) {
                    if (game.state.priorityPlayerId == null) break
                    game.passPriority()
                }
            }

            test("dying fetches a basic land onto the battlefield tapped") {
                val game = board().build()
                val forest = game.state.getLibrary(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
                }
                val bears = game.state.getLibrary(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                }

                killTheEmissary(game)

                withClue("the dies trigger asks first — it's a 'you may'") {
                    game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                }
                game.answerYesNo(true).error shouldBe null

                val search = game.getPendingDecision()
                search.shouldBeInstanceOf<SelectCardsDecision>()
                withClue("only basic lands are findable") {
                    search.options shouldContain forest
                    search.options shouldNotContain bears
                }
                game.selectCards(listOf(forest)).error shouldBe null
                game.resolveStack()

                withClue("the land arrives on the battlefield, not in hand") {
                    game.isOnBattlefield("Forest") shouldBe true
                    game.isInHand(1, "Forest") shouldBe false
                }
                withClue("...and it arrives tapped") {
                    game.state.getEntity(game.findPermanent("Forest")!!)?.has<TappedComponent>() shouldBe true
                }
            }

            test("declining the may-search fetches nothing") {
                val game = board().build()
                killTheEmissary(game)

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("declining leaves the library alone") {
                    game.isOnBattlefield("Forest") shouldBe false
                    game.isInHand(1, "Forest") shouldBe false
                }
            }
        }
    }
}
