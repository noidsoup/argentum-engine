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
 * Scenario tests for Skullsnatcher (BOK #84) — {1}{B} 2/1 Rat Ninja.
 *
 * "Ninjutsu {B}
 *  Whenever this creature deals combat damage to a player, exile up to two target cards from that
 *  player's graveyard."
 *
 * Covers ninjutsu (the shared declare-blockers alternative-cost pipeline) and the targeted combat
 * damage trigger — including that it is scoped to the *damaged* player's graveyard and that "up to
 * two" lets the controller choose nothing (ruling 2005-02-01).
 */
class SkullsnatcherScenarioTest : ScenarioTestBase() {

    init {
        context("Skullsnatcher") {

            /** Pass priority until a decision surfaces (the trigger's target selection). */
            fun advanceToDecision(game: TestGame) {
                var guard = 0
                while (!game.hasPendingDecision() && guard++ < 20) {
                    if (game.state.priorityPlayerId == null) break
                    game.passPriority()
                }
            }

            fun combatBoard() = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Skullsnatcher")
                .withCardInGraveyard(2, "Grizzly Bears")
                .withCardInGraveyard(2, "Hill Giant")
                .withCardInGraveyard(2, "Mountain")
                .withCardInGraveyard(1, "Glory Seeker")
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(2, "Swamp")
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

            test("combat damage exiles two chosen cards from the damaged player's graveyard") {
                val game = combatBoard().build()

                game.declareAttackers(mapOf("Skullsnatcher" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers().error shouldBe null
                advanceToDecision(game)

                val bears = game.findCardsInGraveyard(2, "Grizzly Bears").first()
                val giant = game.findCardsInGraveyard(2, "Hill Giant").first()
                val select = game.selectTargets(listOf(bears, giant))
                withClue("two cards from the damaged player's graveyard is legal: ${select.error}") {
                    select.error shouldBe null
                }
                game.resolveStack()

                withClue("both chosen cards left the graveyard") {
                    game.graveyardSize(2) shouldBe 1
                }
                withClue("both chosen cards are in exile") {
                    game.isInExile(2, "Grizzly Bears") shouldBe true
                    game.isInExile(2, "Hill Giant") shouldBe true
                }
                withClue("the attacker's own graveyard is untouched") {
                    game.graveyardSize(1) shouldBe 1
                }
            }

            test("up to two means the controller may exile nothing") {
                val game = combatBoard().build()

                game.declareAttackers(mapOf("Skullsnatcher" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers().error shouldBe null
                advanceToDecision(game)

                game.skipTargets().error shouldBe null
                game.resolveStack()

                withClue("declining leaves the graveyard intact") {
                    game.graveyardSize(2) shouldBe 3
                }
            }

            test("ninjutsu returns an unblocked attacker and enters tapped and attacking") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Skullsnatcher")
                    .withCardOnBattlefield(1, "Savannah Lions")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withCardInLibrary(1, "Swamp")
                    .withCardInLibrary(2, "Swamp")
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

                val snatcherCardId = game.findCardsInHand(1, "Skullsnatcher").first()
                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = snatcherCardId,
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

                val snatcher = game.findPermanent("Skullsnatcher")
                snatcher shouldNotBe null
                snatcher!!
                withClue("Skullsnatcher entered tapped") {
                    game.state.getEntity(snatcher)?.has<TappedComponent>() shouldBe true
                }
                val attacking = game.state.getEntity(snatcher)?.get<AttackingComponent>()
                withClue("Skullsnatcher entered attacking the same defender") {
                    attacking shouldNotBe null
                    attacking!!.defenderId shouldBe game.player2Id
                }
            }
        }
    }
}
