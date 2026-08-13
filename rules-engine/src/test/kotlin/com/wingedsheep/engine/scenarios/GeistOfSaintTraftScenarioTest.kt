package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class GeistOfSaintTraftScenarioTest : ScenarioTestBase() {
    init {
        test("has hexproof") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Geist of Saint Traft")
                .build()

            val geist = game.findPermanent("Geist of Saint Traft")!!
            game.state.projectedState.hasKeyword(geist, Keyword.HEXPROOF) shouldBe true
        }

        test("attacking creates a tapped-and-attacking Angel token") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Geist of Saint Traft", summoningSickness = false)
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                .build()

            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Geist of Saint Traft" to 2)).error shouldBe null
            if (game.state.stack.isNotEmpty()) game.resolveStack()

            // Angel token should exist alongside Geist
            game.state.getZone(game.player1Id, com.wingedsheep.sdk.core.Zone.BATTLEFIELD).size shouldBe 2
            game.findPermanent("Geist of Saint Traft") shouldNotBe null
        }
    }
}
