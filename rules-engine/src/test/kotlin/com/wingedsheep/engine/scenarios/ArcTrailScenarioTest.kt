package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Arc Trail (SOM #81) — {1}{R} Sorcery.
 *
 * "Arc Trail deals 2 damage to any target and 1 damage to any other target."
 *
 * Covers the split-damage shape and the `TargetOther` constraint that keeps both halves off the
 * same object, plus the mixed creature/player case that `AnyTarget` has to allow.
 */
class ArcTrailScenarioTest : ScenarioTestBase() {

    init {
        context("Arc Trail") {

            fun damageOn(game: TestGame, id: EntityId): Int =
                game.state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

            fun castArcTrail(game: TestGame, targets: List<ChosenTarget>) = run {
                val cardId = game.findCardsInHand(1, "Arc Trail").first()
                val result = game.execute(
                    CastSpell(playerId = game.player1Id, cardId = cardId, targets = targets)
                )
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                result
            }

            test("deals 2 to the first target and 1 to the second") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Arc Trail")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardOnBattlefield(2, "Force of Nature")   // 5/5, survives 2
                    .withCardOnBattlefield(2, "Centaur Courser")   // 3/3, survives 1
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val big = game.findPermanent("Force of Nature")!!
                val small = game.findPermanent("Centaur Courser")!!

                val cast = castArcTrail(
                    game,
                    listOf(ChosenTarget.Permanent(big), ChosenTarget.Permanent(small))
                )
                withClue("Casting Arc Trail at two different creatures should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("first target takes 2") { damageOn(game, big) shouldBe 2 }
                withClue("second target takes 1") { damageOn(game, small) shouldBe 1 }
            }

            test("the two halves may be split across a creature and a player") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Arc Trail")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                val lifeBefore = game.getLifeTotal(2)

                val cast = castArcTrail(
                    game,
                    listOf(ChosenTarget.Player(game.player2Id), ChosenTarget.Permanent(courser))
                )
                withClue("Player + creature is a legal pair of 'any target's: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("the opponent takes the 2-damage half") {
                    game.getLifeTotal(2) shouldBe lifeBefore - 2
                }
                withClue("the creature takes the 1-damage half") {
                    damageOn(game, courser) shouldBe 1
                }
            }

            test("both halves cannot be aimed at the same object") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Arc Trail")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardOnBattlefield(2, "Force of Nature")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val big = game.findPermanent("Force of Nature")!!

                val cast = castArcTrail(
                    game,
                    listOf(ChosenTarget.Permanent(big), ChosenTarget.Permanent(big))
                )
                withClue("'any other target' must reject the first target") {
                    cast.error shouldNotBe null
                }
            }
        }
    }
}
