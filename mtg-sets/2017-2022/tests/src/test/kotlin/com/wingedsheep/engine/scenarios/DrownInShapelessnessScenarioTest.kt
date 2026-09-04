package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Drown in Shapelessness — GS1 #15 · return target creature to its owner's hand */
class DrownInShapelessnessScenarioTest : ScenarioTestBase() {

    init {
        test("returns target creature to its owner's hand") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Drown in Shapelessness")
                .withLandsOnBattlefield(1, "Island", 2)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!

            game.castSpell(1, "Drown in Shapelessness", bears).error shouldBe null
            game.resolveStack()

            withClue("Grizzly Bears returns to the opponent's hand") {
                game.isOnBattlefield("Grizzly Bears") shouldBe false
                game.isInHand(2, "Grizzly Bears") shouldBe true
            }
        }
    }
}
