package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Reckless Pangolin — Global Series: Jiang Yanggu & Mu Yanling #26
 * {2}{G} Creature — Pangolin, 2/2
 *
 * Whenever this creature attacks, it gets +1/+1 until end of turn.
 */
class RecklessPangolinScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        test("gets +1/+1 until end of turn when it attacks") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Reckless Pangolin", summoningSickness = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val pangolin = game.findPermanent("Reckless Pangolin")!!

            withClue("printed stats before attacking") {
                projector.getProjectedPower(game.state, pangolin) shouldBe 2
                projector.getProjectedToughness(game.state, pangolin) shouldBe 2
            }

            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Reckless Pangolin" to 2)).error shouldBe null
            game.resolveStack()

            withClue("the attack trigger grants +1/+1 until end of turn") {
                projector.getProjectedPower(game.state, pangolin) shouldBe 3
                projector.getProjectedToughness(game.state, pangolin) shouldBe 3
            }
        }
    }
}
