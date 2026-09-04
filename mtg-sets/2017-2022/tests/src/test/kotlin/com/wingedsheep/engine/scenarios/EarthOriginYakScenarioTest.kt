package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Earth-Origin Yak — GS1 #9 · ETB creatures you control get +1/+1 until end of turn */
class EarthOriginYakScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        test("when it enters, creatures you control get +1/+1 until end of turn") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Earth-Origin Yak")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Plains", 4)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!

            game.castSpell(1, "Earth-Origin Yak").error shouldBe null
            game.resolveStack()

            val yak = game.findPermanent("Earth-Origin Yak")!!

            withClue("Grizzly Bears gets +1/+1") {
                projector.getProjectedPower(game.state, bears) shouldBe 3
                projector.getProjectedToughness(game.state, bears) shouldBe 3
            }
            withClue("Earth-Origin Yak gets +1/+1 from its own ETB") {
                projector.getProjectedPower(game.state, yak) shouldBe 3
                projector.getProjectedToughness(game.state, yak) shouldBe 5
            }
        }
    }
}
