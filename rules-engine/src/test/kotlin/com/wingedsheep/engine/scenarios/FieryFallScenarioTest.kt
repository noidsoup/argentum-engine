package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.TypecycleCard
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Fiery Fall (CON #63) — {5}{R} Instant.
 *
 * "Fiery Fall deals 5 damage to target creature.
 *  Basic landcycling {1}{R}"
 *
 * Two independent halves worth pinning: the damage amount is bracketed from both sides (a 5/5 dies,
 * a 3/6 survives), and basic landcycling is a typecycling ability whose search filter is *any* basic
 * land — not just red ones — so a Forest is a legal find for a red card.
 */
class FieryFallScenarioTest : ScenarioTestBase() {

    init {
        context("Fiery Fall") {

            test("deals exactly 5 damage — a 5/5 dies") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Fiery Fall")
                    .withLandsOnBattlefield(1, "Mountain", 6)
                    .withCardOnBattlefield(2, "Force of Nature")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val target = game.findPermanent("Force of Nature")!!
                game.castSpell(1, "Fiery Fall", target).error shouldBe null
                game.resolveStack()

                withClue("5 damage is lethal to a 5/5") {
                    game.isOnBattlefield("Force of Nature") shouldBe false
                    game.isInGraveyard(2, "Force of Nature") shouldBe true
                }
            }

            test("deals exactly 5 damage — a 3/6 survives") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Fiery Fall")
                    .withLandsOnBattlefield(1, "Mountain", 6)
                    .withCardOnBattlefield(2, "Etherium-Horn Sorcerer")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val target = game.findPermanent("Etherium-Horn Sorcerer")!!
                game.castSpell(1, "Fiery Fall", target).error shouldBe null
                game.resolveStack()

                withClue("5 damage is not lethal to a 3/6") {
                    game.isOnBattlefield("Etherium-Horn Sorcerer") shouldBe true
                }
            }

            test("basic landcycling discards the card and fetches any basic land") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Fiery Fall")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val library = game.state.getLibrary(game.player1Id)
                fun inLibrary(name: String) = library.first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == name
                }
                val forest = inLibrary("Forest")
                val bears = inLibrary("Grizzly Bears")

                val fieryFall = game.findCardsInHand(1, "Fiery Fall").first()
                game.execute(TypecycleCard(playerId = game.player1Id, cardId = fieryFall)).error shouldBe null

                withClue("cycling discards Fiery Fall as part of the cost") {
                    game.isInGraveyard(1, "Fiery Fall") shouldBe true
                    game.isInHand(1, "Fiery Fall") shouldBe false
                }

                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                withClue("any basic land is findable — a Forest counts for a red card") {
                    decision.options shouldContain forest
                }
                withClue("nonland cards are not findable") {
                    decision.options shouldNotContain bears
                }

                game.selectCards(listOf(forest)).error shouldBe null

                withClue("the basic land goes to hand, not the battlefield") {
                    game.isInHand(1, "Forest") shouldBe true
                    game.isOnBattlefield("Forest") shouldBe false
                }
            }
        }
    }
}
