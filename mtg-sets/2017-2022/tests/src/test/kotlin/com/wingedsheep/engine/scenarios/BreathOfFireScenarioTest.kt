package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Breath of Fire — GS1 #33 · deals 2 damage to target creature */
class BreathOfFireScenarioTest : ScenarioTestBase() {

    init {
        test("deals 2 damage to target creature") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Breath of Fire")
                .withLandsOnBattlefield(1, "Mountain", 2)
                .withCardOnBattlefield(2, "Typhoid Rats")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val rats = game.findPermanent("Typhoid Rats")!!

            game.castSpell(1, "Breath of Fire", rats).error shouldBe null
            game.resolveStack()

            withClue("2 damage kills a 1/1") {
                game.isOnBattlefield("Typhoid Rats") shouldBe false
                game.isInGraveyard(2, "Typhoid Rats") shouldBe true
            }
        }
    }
}
