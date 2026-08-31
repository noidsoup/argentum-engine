package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Screeching Phoenix — Global Series: Jiang Yanggu & Mu Yanling #30
 * {2}{R}: Creatures you control get +1/+0 until end of turn.
 */
class ScreechingPhoenixScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        test("the {2}{R} ability gives creatures you control +1/+0 until end of turn") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Screeching Phoenix")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Mountain", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val phoenix = game.findPermanent("Screeching Phoenix")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val pump = cardRegistry.getCard("Screeching Phoenix")!!.script.activatedAbilities.first()

            game.execute(
                ActivateAbility(playerId = game.player1Id, sourceId = phoenix, abilityId = pump.id),
            ).error shouldBe null
            game.resolveStack()

            withClue("Screeching Phoenix gets +1/+0") {
                projector.getProjectedPower(game.state, phoenix) shouldBe 5
                projector.getProjectedToughness(game.state, phoenix) shouldBe 4
            }
            withClue("other creatures you control also get +1/+0") {
                projector.getProjectedPower(game.state, bears) shouldBe 3
                projector.getProjectedToughness(game.state, bears) shouldBe 2
            }
        }
    }
}
