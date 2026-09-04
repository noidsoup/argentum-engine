package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Dragon's Presence — GS1 #16 · 5 damage to target attacking or blocking creature */
class DragonsPresenceScenarioTest : ScenarioTestBase() {

    init {
        test("deals 5 damage to target attacking creature") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Dragon's Presence")
                .withLandsOnBattlefield(1, "Plains", 3)
                .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Hill Giant" to 2)).error shouldBe null

            val giant = game.findPermanent("Hill Giant")!!
            game.castSpell(1, "Dragon's Presence", giant).error shouldBe null
            game.resolveStack()

            withClue("5 damage kills a 3/3 attacker") {
                game.isOnBattlefield("Hill Giant") shouldBe false
                game.isInGraveyard(1, "Hill Giant") shouldBe true
            }
        }
    }
}
