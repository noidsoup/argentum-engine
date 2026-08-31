package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Purple-Crystal Crab — Global Series: Jiang Yanggu & Mu Yanling #3
 * {1}{U} Creature — Crab, 1/1
 *
 * When this creature dies, draw a card.
 */
class PurpleCrystalCrabScenarioTest : ScenarioTestBase() {

    init {
        test("draws a card when it dies") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Purple-Crystal Crab", summoningSickness = false)
                .withCardInHand(1, "Doom Blade")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withCardInLibrary(1, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val crab = game.findPermanent("Purple-Crystal Crab")!!
            val handBefore = game.handSize(1)

            game.castSpell(1, "Doom Blade", crab).error shouldBe null
            game.resolveStack() // destroy the crab
            game.resolveStack() // resolve the dies trigger

            withClue("Purple-Crystal Crab died") {
                game.isOnBattlefield("Purple-Crystal Crab") shouldBe false
                game.isInGraveyard(1, "Purple-Crystal Crab") shouldBe true
            }
            withClue("dying draws a card") {
                game.handSize(1) shouldBe handBefore
            }
        }
    }
}
