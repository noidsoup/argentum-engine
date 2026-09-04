package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Confidence from Strength — GS1 #35 · +4/+4 and trample until end of turn */
class ConfidenceFromStrengthScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        test("target creature gets +4/+4 and gains trample until end of turn") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Confidence from Strength")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Forest", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bear = game.findPermanent("Grizzly Bears")!!

            game.castSpell(1, "Confidence from Strength", bear).error shouldBe null
            game.resolveStack()

            withClue("the targeted creature gets +4/+4") {
                projector.getProjectedPower(game.state, bear) shouldBe 6
                projector.getProjectedToughness(game.state, bear) shouldBe 6
            }
            withClue("the targeted creature gains trample") {
                game.state.projectedState.hasKeyword(bear, Keyword.TRAMPLE) shouldBe true
            }
        }
    }
}
