package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Rhythmic Water Vortex — Global Series: Jiang Yanggu & Mu Yanling #18
 * Return up to two target creatures to their owners' hands, then search for Mu Yanling.
 */
class RhythmicWaterVortexScenarioTest : ScenarioTestBase() {

    init {
        test("bounces a creature and tutors Mu Yanling from the library") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Rhythmic Water Vortex")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardInLibrary(1, "Mu Yanling")
                .withLandsOnBattlefield(1, "Island", 5)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            val yanlingInLibrary = game.state.getLibrary(game.player1Id).single { id ->
                game.state.getEntity(id)?.get<CardComponent>()?.name == "Mu Yanling"
            }

            game.castSpell(1, "Rhythmic Water Vortex", bears).error shouldBe null
            game.resolveStack()

            withClue("Grizzly Bears returns to its owner's hand") {
                game.isOnBattlefield("Grizzly Bears") shouldBe false
                game.isInHand(2, "Grizzly Bears") shouldBe true
            }

            withClue("search for Mu Yanling") {
                game.hasPendingDecision() shouldBe true
            }
            game.selectCards(listOf(yanlingInLibrary))
            game.resolveStack()

            withClue("Mu Yanling is tutored to hand") {
                game.isInHand(1, "Mu Yanling") shouldBe true
            }
        }
    }
}
