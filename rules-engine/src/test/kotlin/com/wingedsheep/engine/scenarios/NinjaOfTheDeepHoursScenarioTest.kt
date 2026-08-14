package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Ninja of the Deep Hours (BOK #44) — {3}{U} 2/2 Human Ninja.
 *
 * "Ninjutsu {1}{U}
 *  Whenever this creature deals combat damage to a player, you may draw a card."
 *
 * Covers ninjutsu (the shared declare-blockers alternative-cost pipeline) and both branches of the
 * untargeted "you may draw" — accepting draws exactly one card, declining draws none.
 */
class NinjaOfTheDeepHoursScenarioTest : ScenarioTestBase() {

    init {
        context("Ninja of the Deep Hours") {

            /** Pass priority until the may-draw decision surfaces. */
            fun advanceToDecision(game: TestGame) {
                var guard = 0
                while (!game.hasPendingDecision() && guard++ < 20) {
                    if (game.state.priorityPlayerId == null) break
                    game.passPriority()
                }
            }

            fun combatBoard() = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Ninja of the Deep Hours")
                .withCardInLibrary(1, "Grizzly Bears")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

            fun swingUnblocked(game: TestGame) {
                game.declareAttackers(mapOf("Ninja of the Deep Hours" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers().error shouldBe null
                advanceToDecision(game)
            }

            test("combat damage to a player offers a draw, and accepting draws one card") {
                val game = combatBoard().build()
                val handBefore = game.handSize(1)
                val librarySizeBefore = game.librarySize(1)
                val opponentLifeBefore = game.getLifeTotal(2)

                swingUnblocked(game)

                withClue("the may-draw trigger should raise a yes/no decision") {
                    (game.getPendingDecision() is YesNoDecision) shouldBe true
                }
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()

                withClue("accepting draws exactly one card") {
                    game.handSize(1) shouldBe handBefore + 1
                    game.librarySize(1) shouldBe librarySizeBefore - 1
                }
                withClue("the opponent took the 2 combat damage") {
                    game.getLifeTotal(2) shouldBe opponentLifeBefore - 2
                }
            }

            test("declining the may-draw leaves hand and library untouched") {
                val game = combatBoard().build()
                val handBefore = game.handSize(1)
                val librarySizeBefore = game.librarySize(1)

                swingUnblocked(game)

                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("declining draws nothing") {
                    game.handSize(1) shouldBe handBefore
                    game.librarySize(1) shouldBe librarySizeBefore
                }
            }

            test("ninjutsu returns an unblocked attacker and enters tapped and attacking") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Ninja of the Deep Hours")
                    .withCardOnBattlefield(1, "Savannah Lions")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val lions = game.findPermanent("Savannah Lions")!!
                game.declareAttackers(mapOf("Savannah Lions" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers().error shouldBe null

                // CR 509.1 — hand priority back to the active player for the declare-blockers window.
                var guard = 0
                while (game.state.priorityPlayerId != null && game.state.priorityPlayerId != game.player1Id &&
                    game.state.step == Step.DECLARE_BLOCKERS && guard++ < 4
                ) {
                    game.passPriority()
                }

                val ninjaCardId = game.findCardsInHand(1, "Ninja of the Deep Hours").first()
                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = ninjaCardId,
                        useAlternativeCost = true,
                        alternativeCostType = AlternativeCostType.SNEAK,
                        additionalCostPayment = AdditionalCostPayment(bouncedPermanents = listOf(lions))
                    )
                )
                withClue("Ninjutsu cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("the unblocked attacker went back to hand") {
                    game.findPermanent("Savannah Lions") shouldBe null
                    game.state.getHand(game.player1Id).any { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Savannah Lions"
                    } shouldBe true
                }

                val ninja = game.findPermanent("Ninja of the Deep Hours")
                ninja shouldNotBe null
                ninja!!
                withClue("the Ninja entered tapped") {
                    game.state.getEntity(ninja)?.has<TappedComponent>() shouldBe true
                }
                val attacking = game.state.getEntity(ninja)?.get<AttackingComponent>()
                withClue("the Ninja entered attacking the same defender") {
                    attacking shouldNotBe null
                    attacking!!.defenderId shouldBe game.player2Id
                }
            }
        }
    }
}
