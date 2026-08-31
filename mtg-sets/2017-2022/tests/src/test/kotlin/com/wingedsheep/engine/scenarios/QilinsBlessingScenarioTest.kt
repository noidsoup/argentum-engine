package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Qilin's Blessing — Global Series: Jiang Yanggu & Mu Yanling #14
 * {W} Instant
 *
 * Target creature gets +2/+2 until end of turn.
 */
class QilinsBlessingScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        test("target creature gets +2/+2 until end of turn") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Qilin's Blessing")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Plains", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bear = game.findPermanent("Grizzly Bears")!!

            game.castSpell(1, "Qilin's Blessing", bear).error shouldBe null
            game.resolveStack()

            withClue("the targeted creature gets +2/+2") {
                projector.getProjectedPower(game.state, bear) shouldBe 4
                projector.getProjectedToughness(game.state, bear) shouldBe 4
            }
        }
    }
}
