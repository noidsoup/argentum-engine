package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/** Cloak of Mists — GS1 reprint · enchanted creature can't be blocked */
class CloakOfMistsScenarioTest : ScenarioTestBase() {

    init {
        context("the enchanted creature cannot be blocked") {

            test("no creature may block the enchanted attacker") {
                val game = combatScenario()

                val result = game.declareBlockers(mapOf("Grizzly Bears" to listOf("Hill Giant")))

                withClue("Cloak of Mists must make the creature it enchants unblockable") {
                    result.error shouldNotBe null
                }
            }

            test("an unenchanted attacker is still blockable") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Hill Giant" to 2)).error shouldBe null
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                withClue("the restriction must come from the Aura and not from the creature") {
                    game.declareBlockers(mapOf("Grizzly Bears" to listOf("Hill Giant"))).error shouldBe null
                }
            }
        }
    }

    private fun combatScenario() = scenario()
        .withPlayers("Player1", "Player2")
        .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
        .withCardAttachedTo(1, "Cloak of Mists", "Hill Giant")
        .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
        .withActivePlayer(1)
        .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
        .build()
        .also { game ->
            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Hill Giant" to 2)).error shouldBe null
            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
        }
}
