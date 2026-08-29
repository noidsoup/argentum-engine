package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Hellion Eruption (ROE {5}{R} sorcery).
 *
 * Sacrifice all creatures you control, then create that many 4/4 red Hellion creature tokens.
 */
class HellionEruptionScenarioTest : ScenarioTestBase() {

    init {
        context("Hellion Eruption") {
            test("sacrifices all your creatures and creates one 4/4 Hellion per sacrifice") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Hellion Eruption")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Elvish Warrior")
                    .withLandsOnBattlefield(1, "Mountain", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Hellion Eruption").error shouldBe null
                game.resolveStack()

                withClue("sacrificed creatures are in the graveyard") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isOnBattlefield("Elvish Warrior") shouldBe false
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                    game.isInGraveyard(1, "Elvish Warrior") shouldBe true
                }

                val projected = game.state.projectedState
                val hellionTokens = game.state.getBattlefield().filter { id ->
                    projected.getSubtypes(id).any { it.equals("Hellion", ignoreCase = true) } &&
                        projected.getPower(id) == 4 &&
                        projected.getToughness(id) == 4 &&
                        projected.getController(id) == game.player1Id
                }
                withClue("creates one 4/4 Hellion token per sacrificed creature") {
                    hellionTokens.size shouldBe 2
                }
            }

            test("creates no tokens when you control no creatures") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Hellion Eruption")
                    .withLandsOnBattlefield(1, "Mountain", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val battlefieldBefore = game.state.getBattlefield().size
                game.castSpell(1, "Hellion Eruption").error shouldBe null
                game.resolveStack()

                withClue("no new permanents appear on an empty board") {
                    game.state.getBattlefield().size shouldBe battlefieldBefore
                }
            }
        }
    }
}
