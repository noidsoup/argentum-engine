package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Krond the Dawn-Clad — {G}{G}{G}{W}{W}{W} Legendary Creature — Archon 6/6
 *
 * Flying, vigilance
 * Whenever Krond attacks, if it's enchanted, exile target permanent.
 */
class KrondTheDawnCladScenarioTest : ScenarioTestBase() {

    init {
        context("Krond the Dawn-Clad") {

            fun advanceToDecision(game: TestGame) {
                var guard = 0
                while (!game.hasPendingDecision() && guard++ < 20) {
                    if (game.state.priorityPlayerId == null) break
                    game.passPriority()
                }
            }

            test("attacking while enchanted exiles a target permanent") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Krond the Dawn-Clad")
                    .withCardAttachedTo(1, "Rancor", "Krond the Dawn-Clad")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Krond the Dawn-Clad" to 2)).error shouldBe null
                advanceToDecision(game)

                val bears = game.findPermanent("Grizzly Bears")!!
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears was exiled") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInExile(2, "Grizzly Bears") shouldBe true
                }
            }

            test("attacking without an Aura does not exile anything") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Krond the Dawn-Clad")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Krond the Dawn-Clad" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers().error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                game.resolveStack()

                withClue("no exile trigger without an Aura on Krond") {
                    game.hasPendingDecision() shouldBe false
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
