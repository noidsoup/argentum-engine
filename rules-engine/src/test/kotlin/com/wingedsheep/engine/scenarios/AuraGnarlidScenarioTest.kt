package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Aura Gnarlid (ROE #175 / PC2 #55) — evasion keyed to its power and +1/+1 for each Aura on the
 * battlefield.
 */
class AuraGnarlidScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(
            CardDefinition.creature("Small Blocker", ManaCost.parse("{1}"), emptySet(), power = 1, toughness = 1),
        )

        context("Aura Gnarlid") {

            test("gets +1/+1 for each Aura on the battlefield") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Aura Gnarlid")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Pacifism", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val gnarlid = game.findPermanent("Aura Gnarlid")!!
                val projected = game.state.projectedState
                withClue("one battlefield Aura makes Aura Gnarlid a 3/3") {
                    projected.getPower(gnarlid) shouldBe 3
                    projected.getToughness(gnarlid) shouldBe 3
                }
            }

            test("a lower-power blocker cannot block the buffed gnarlid") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Aura Gnarlid")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Pacifism", "Grizzly Bears")
                    .withCardOnBattlefield(2, "Small Blocker")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Aura Gnarlid" to 2))
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                val block = game.declareBlockers(mapOf("Small Blocker" to listOf("Aura Gnarlid")))
                withClue("a power-1 blocker cannot block a power-3 Aura Gnarlid") {
                    block.error shouldNotBe null
                }
            }
        }
    }
}
