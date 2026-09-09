package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.PairedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Imperious Mindbreaker (VOC #33) — soulbond grants attack-mill equal to the attacking creature's
 * toughness.
 */
class ImperiousMindbreakerScenarioTest : ScenarioTestBase() {

    init {
        context("Imperious Mindbreaker") {

            test("while paired, a partner's attack mills the opponent equal to its toughness") {
                var builder = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Imperious Mindbreaker", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(15) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val mindbreaker = game.findPermanent("Imperious Mindbreaker")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state = game.state
                    .updateEntity(mindbreaker) { it.with(PairedComponent(bears)) }
                    .updateEntity(bears) { it.with(PairedComponent(mindbreaker)) }

                val libraryBefore = game.librarySize(2)

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.hasPendingDecision()) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("Grizzly Bears is 2/2, so the opponent mills two cards") {
                    game.librarySize(2) shouldBe libraryBefore - 2
                }
            }

            test("unpaired Mindbreaker does not mill on attack") {
                var builder = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Imperious Mindbreaker", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(15) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val libraryBefore = game.librarySize(2)

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Imperious Mindbreaker" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.hasPendingDecision()) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("soulbondPair is empty while unpaired, so no mill trigger fires") {
                    game.librarySize(2) shouldBe libraryBefore
                }
            }
        }
    }
}
