package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
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
 * Scenario tests for Mistblade Shinobi (BOK #43) — {2}{U} 1/1 Human Ninja.
 *
 * "Ninjutsu {U}
 *  Whenever this creature deals combat damage to a player, you may return target creature that
 *  player controls to its owner's hand."
 *
 * The interesting part is the target scope: "that player" is the *damaged* player, modeled with
 * `controlledByTriggeringPlayer()`. These tests pin that the attacker's own creatures are never
 * offered, that the bounce sends the creature to its owner's hand, and that the "may" can be
 * declined. Ninjutsu itself is covered too, since it is the card's other ability.
 */
class MistbladeShinobiScenarioTest : ScenarioTestBase() {

    init {
        context("Mistblade Shinobi") {

            /** Pass priority until the trigger's decision surfaces. */
            fun advanceToDecision(game: TestGame) {
                var guard = 0
                while (!game.hasPendingDecision() && guard++ < 20) {
                    if (game.state.priorityPlayerId == null) break
                    game.passPriority()
                }
            }

            fun combatBoard() = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Mistblade Shinobi")
                .withCardOnBattlefield(1, "Savannah Lions")     // attacker's own — must not be a legal target
                .withCardOnBattlefield(2, "Grizzly Bears")      // damaged player's — the legal target
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(2, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

            fun swingUnblocked(game: TestGame) {
                game.declareAttackers(mapOf("Mistblade Shinobi" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers().error shouldBe null
                advanceToDecision(game)
            }

            test("combat damage bounces a creature the damaged player controls to its owner's hand") {
                val game = combatBoard().build()
                swingUnblocked(game)

                // The may-question comes first (CR 603.3d ordering: consent, then targets).
                game.answerYesNo(true).error shouldBe null

                val bears = game.findPermanent("Grizzly Bears")!!
                val select = game.selectTargets(listOf(bears))
                withClue("a creature the damaged player controls is a legal target: ${select.error}") {
                    select.error shouldBe null
                }
                game.resolveStack()

                withClue("the bounced creature left the battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                withClue("...and went to its owner's hand, not the attacker's") {
                    game.isInHand(2, "Grizzly Bears") shouldBe true
                    game.isInHand(1, "Grizzly Bears") shouldBe false
                }
            }

            test("the attacker's own creature is not a legal target — 'that player' is the damaged player") {
                val game = combatBoard().build()
                swingUnblocked(game)

                game.answerYesNo(true).error shouldBe null

                val lions = game.findPermanent("Savannah Lions")!!
                val select = game.selectTargets(listOf(lions))
                withClue("a creature the *attacker* controls must be rejected") {
                    select.error shouldNotBe null
                }
                withClue("Savannah Lions stays on the battlefield") {
                    game.isOnBattlefield("Savannah Lions") shouldBe true
                }
            }

            test("declining the may leaves the damaged player's board intact") {
                val game = combatBoard().build()
                swingUnblocked(game)

                game.answerYesNo(false).error shouldBe null
                game.resolveStack()

                withClue("declining bounces nothing") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.isInHand(2, "Grizzly Bears") shouldBe false
                }
            }

            test("ninjutsu returns an unblocked attacker and enters tapped and attacking") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Mistblade Shinobi")
                    .withCardOnBattlefield(1, "Savannah Lions")
                    .withLandsOnBattlefield(1, "Island", 1)
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

                val shinobiCardId = game.findCardsInHand(1, "Mistblade Shinobi").first()
                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = shinobiCardId,
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

                val shinobi = game.findPermanent("Mistblade Shinobi")
                shinobi shouldNotBe null
                shinobi!!
                withClue("the Shinobi entered tapped") {
                    game.state.getEntity(shinobi)?.has<TappedComponent>() shouldBe true
                }
                val attacking = game.state.getEntity(shinobi)?.get<AttackingComponent>()
                withClue("the Shinobi entered attacking the same defender") {
                    attacking shouldNotBe null
                    attacking!!.defenderId shouldBe game.player2Id
                }
            }
        }
    }
}
