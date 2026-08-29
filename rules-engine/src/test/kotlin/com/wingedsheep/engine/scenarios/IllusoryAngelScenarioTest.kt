package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Illusory Angel (PC2 #19) — Cast only if you've cast another spell this turn. Flying 4/4.
 */
class IllusoryAngelScenarioTest : ScenarioTestBase() {

    init {
        context("Illusory Angel") {

            test("cannot be cast as the first spell of the turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Illusory Angel")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val result = game.castSpell(1, "Illusory Angel")
                withClue("no prior spell this turn") {
                    result.error shouldNotBe null
                }
            }

            test("can be cast after another spell and enters as a 4/4 flyer") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Illusory Angel")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("cast Lightning Bolt first") {
                    val bear = game.findPermanent("Grizzly Bears")!!
                    game.castSpell(1, "Lightning Bolt", bear).error shouldBe null
                    game.resolveStack()
                }

                withClue("Illusory Angel is legal after another spell") {
                    game.castSpell(1, "Illusory Angel").error shouldBe null
                    game.resolveStack()
                }

                val angel = game.findPermanent("Illusory Angel")!!
                withClue("4/4") {
                    game.state.projectedState.getPower(angel) shouldBe 4
                    game.state.projectedState.getToughness(angel) shouldBe 4
                }
                withClue("flying") {
                    game.state.projectedState.hasKeyword(angel, Keyword.FLYING) shouldBe true
                }
            }
        }
    }
}
