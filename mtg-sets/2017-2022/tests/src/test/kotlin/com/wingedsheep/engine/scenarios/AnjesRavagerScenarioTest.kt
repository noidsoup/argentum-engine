package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Anje's Ravager (C19 #22) — "Whenever this creature attacks, discard your hand, then draw three cards."
 */
class AnjesRavagerScenarioTest : ScenarioTestBase() {

    init {
        test("attack trigger discards the hand then draws three cards") {
            var builder = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Anje's Ravager", summoningSickness = false)
                .withCardInHand(1, "Mountain")
                .withCardInHand(1, "Mountain")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
            val game = builder.build()

            game.handSize(1) shouldBe 2

            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Anje's Ravager" to 2)).error shouldBe null
            game.resolveStack()

            withClue("two Mountains were discarded, then three cards were drawn") {
                game.handSize(1) shouldBe 3
                game.graveyardSize(1) shouldBe 2
            }
        }
    }
}
