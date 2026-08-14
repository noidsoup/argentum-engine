package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Glen Elendra Liege (SHM #163) — {1}{U/B}{U/B}{U/B} 2/3 Faerie Knight.
 *
 * "Flying
 *  Other blue creatures you control get +1/+1.
 *  Other black creatures you control get +1/+1."
 *
 * The two lord clauses are separate and cumulative (ruling 2008-05-01), so a creature you control
 * that is both blue and black gets +2/+2. Both exclude the Liege itself, even though it is a blue
 * *and* black creature.
 */
class GlenElendraLiegeScenarioTest : ScenarioTestBase() {

    init {
        context("Glen Elendra Liege") {

            fun power(game: TestGame, id: EntityId) = game.state.projectedState.getPower(id)
            fun toughness(game: TestGame, id: EntityId) = game.state.projectedState.getToughness(id)

            fun board() = scenario()
                .withPlayers("Player", "Opponent")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

            test("pumps other blue creatures you control") {
                val game = board()
                    .withCardOnBattlefield(1, "Glen Elendra Liege")
                    .withCardOnBattlefield(1, "Island Walker") // blue 2/2
                    .build()

                val merfolk = game.findPermanent("Island Walker")!!
                power(game, merfolk) shouldBe 3
                toughness(game, merfolk) shouldBe 3
            }

            test("pumps other black creatures you control") {
                val game = board()
                    .withCardOnBattlefield(1, "Glen Elendra Liege")
                    .withCardOnBattlefield(1, "Black Creature") // black 2/2
                    .build()

                val zombie = game.findPermanent("Black Creature")!!
                power(game, zombie) shouldBe 3
                toughness(game, zombie) shouldBe 3
            }

            test("does not pump itself, and has flying") {
                val game = board()
                    .withCardOnBattlefield(1, "Glen Elendra Liege")
                    .build()

                val liege = game.findPermanent("Glen Elendra Liege")!!
                withClue("both clauses say 'other', even though the Liege is blue and black") {
                    power(game, liege) shouldBe 2
                    toughness(game, liege) shouldBe 3
                }
                game.state.projectedState.hasKeyword(liege, Keyword.FLYING) shouldBe true
            }

            test("the two clauses stack on a creature that is both blue and black") {
                val game = board()
                    .withCardOnBattlefield(1, "Glen Elendra Liege")
                    .withCardOnBattlefield(1, "Glen Elendra Liege")
                    .build()

                // Each Liege is blue and black, so the *other* one's two clauses both apply to it.
                val lieges = game.findAllPermanents("Glen Elendra Liege")
                lieges.size shouldBe 2
                lieges.forEach { liege ->
                    withClue("blue lord + black lord = +2/+2") {
                        power(game, liege) shouldBe 4
                        toughness(game, liege) shouldBe 5
                    }
                }
            }

            test("does not pump creatures an opponent controls") {
                val game = board()
                    .withCardOnBattlefield(1, "Glen Elendra Liege")
                    .withCardOnBattlefield(2, "Island Walker")
                    .build()

                val theirs = game.findPermanent("Island Walker")!!
                power(game, theirs) shouldBe 2
                toughness(game, theirs) shouldBe 2
            }
        }
    }
}
