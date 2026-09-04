package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Heavenly Qilin — GS1 #6 · attack grants flying to another creature you control */
class HeavenlyQilinScenarioTest : ScenarioTestBase() {

    init {
        test("when it attacks, another target creature you control gains flying until end of turn") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Heavenly Qilin", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!

            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Heavenly Qilin" to 2)).error shouldBe null
            game.resolveStack()

            game.selectTargets(listOf(bears)).error shouldBe null
            game.resolveStack()

            withClue("Grizzly Bears gains flying until end of turn") {
                game.state.projectedState.hasKeyword(bears, Keyword.FLYING) shouldBe true
            }
        }
    }
}
