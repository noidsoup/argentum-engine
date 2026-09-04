package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Fire-Omen Crane — GS1 #29 · attack trigger deals 1 damage to opponent creature */
class FireOmenCraneScenarioTest : ScenarioTestBase() {

    init {
        test("when it attacks, deals 1 damage to target creature an opponent controls") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Fire-Omen Crane", summoningSickness = false)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!

            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Fire-Omen Crane" to 2)).error shouldBe null
            game.resolveStack()

            game.selectTargets(listOf(bears)).error shouldBe null
            game.resolveStack()

            withClue("Grizzly Bears takes 1 damage") {
                game.state.getEntity(bears)?.get<DamageComponent>()?.amount shouldBe 1
            }
        }
    }
}
