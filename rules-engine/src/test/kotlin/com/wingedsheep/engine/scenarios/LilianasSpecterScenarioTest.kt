package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.m11.cards.LilianasSpecter
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Liliana's Specter (M11 #104) — {1}{B}{B} Creature — Specter 2/2.
 *
 * Flying. When this creature enters, each opponent discards a card.
 */
class LilianasSpecterScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(LilianasSpecter)

        context("Liliana's Specter") {

            test("ETB makes each opponent discard a card") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Liliana's Specter")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Liliana's Specter").error shouldBe null
                game.resolveStack()

                withClue("specter is on the battlefield") {
                    game.isOnBattlefield("Liliana's Specter") shouldBe true
                }
                withClue("opponent discarded their only card") {
                    game.handSize(2) shouldBe 0
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
