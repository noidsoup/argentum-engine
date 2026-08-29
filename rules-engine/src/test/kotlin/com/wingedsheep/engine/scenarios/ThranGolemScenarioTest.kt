package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Thran Golem (UDS {5} 3/3 Artifact Creature — Golem).
 *
 * As long as this creature is enchanted, it gets +2/+2 and has flying, first strike, and trample.
 */
class ThranGolemScenarioTest : ScenarioTestBase() {

    init {
        context("Thran Golem") {
            test("is a vanilla 3/3 without an Aura") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Thran Golem")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val golem = game.findPermanent("Thran Golem")!!
                val projected = game.state.projectedState
                withClue("base stats without an Aura") {
                    projected.getPower(golem) shouldBe 3
                    projected.getToughness(golem) shouldBe 3
                }
                withClue("no keywords without an Aura") {
                    projected.hasKeyword(golem, Keyword.FLYING) shouldBe false
                    projected.hasKeyword(golem, Keyword.FIRST_STRIKE) shouldBe false
                    projected.hasKeyword(golem, Keyword.TRAMPLE) shouldBe false
                }
            }

            test("gets +2/+2 and keywords while enchanted") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Thran Golem")
                    .withCardAttachedTo(1, "Pacifism", "Thran Golem")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val golem = game.findPermanent("Thran Golem")!!
                val projected = game.state.projectedState
                withClue("one Aura grants +2/+2") {
                    projected.getPower(golem) shouldBe 5
                    projected.getToughness(golem) shouldBe 5
                }
                withClue("enchanted golem has flying, first strike, and trample") {
                    projected.hasKeyword(golem, Keyword.FLYING) shouldBe true
                    projected.hasKeyword(golem, Keyword.FIRST_STRIKE) shouldBe true
                    projected.hasKeyword(golem, Keyword.TRAMPLE) shouldBe true
                }
            }
        }
    }
}
