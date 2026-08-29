package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Spirit Mantle — {1}{W} Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +1/+1 and has protection from creatures.
 */
class SpiritMantleScenarioTest : ScenarioTestBase() {

    init {
        context("Spirit Mantle") {

            test("enchanted creature gets +1/+1 and protection from creatures") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Spirit Mantle", "Grizzly Bears")
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("Grizzly Bears is 3/3") {
                    game.state.projectedState.getPower(bears) shouldBe 3
                    game.state.projectedState.getToughness(bears) shouldBe 3
                }
                withClue("Grizzly Bears has protection from creatures") {
                    game.state.projectedState.hasKeyword(bears, "PROTECTION_FROM_CARDTYPE_CREATURE") shouldBe true
                }
            }

            test("protected creature cannot be blocked by a creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withCardAttachedTo(1, "Spirit Mantle", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                val attack = game.declareAttackers(mapOf("Grizzly Bears" to 2))
                withClue("Grizzly Bears attacks: ${attack.error}") { attack.error shouldBe null }

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                val block = game.declareBlockers(mapOf("Hill Giant" to listOf("Grizzly Bears")))
                withClue("Blocking the creature-protected attacker with a creature is illegal: ${block.error}") {
                    (block.error != null) shouldBe true
                }
            }
        }
    }
}
