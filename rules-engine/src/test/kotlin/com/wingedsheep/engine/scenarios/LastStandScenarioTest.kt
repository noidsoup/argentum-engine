package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Last Stand (APC #107 / PC2 #100) — five-color payoff keyed to basic lands you control.
 */
class LastStandScenarioTest : ScenarioTestBase() {

    init {
        context("Last Stand") {
            fun findCardInHand(game: TestGame, playerNumber: Int, cardName: String): EntityId {
                val playerId = if (playerNumber == 1) game.player1Id else game.player2Id
                return game.state.getHand(playerId).find { entityId ->
                    game.state.getEntity(entityId)?.get<CardComponent>()?.name == cardName
                } ?: error("Card '$cardName' not found in player $playerNumber's hand")
            }

            fun saprolingCount(game: TestGame, playerId: EntityId = game.player1Id): Int =
                game.state.getZone(playerId, Zone.BATTLEFIELD).count { id ->
                    game.state.getEntity(id)?.get<TokenComponent>() != null &&
                        game.state.projectedState.getSubtypes(id)
                            .any { it.equals("Saproling", ignoreCase = true) }
                }

            fun castLastStand(
                game: TestGame,
                opponentCreature: String,
                discardIds: List<EntityId>,
            ) {
                val lastStandId = findCardInHand(game, 1, "Last Stand")
                val creatureId = game.findPermanent(opponentCreature)!!

                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = lastStandId,
                        targets = listOf(
                            ChosenTarget.Player(game.player2Id),
                            ChosenTarget.Permanent(creatureId),
                        ),
                    ),
                ).error shouldBe null

                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                while (game.getPendingDecision() != null) {
                    when (val decision = game.getPendingDecision()) {
                        is SelectCardsDecision -> {
                            game.submitDecision(CardsSelectedResponse(decision.id, discardIds))
                        }
                        is SelectManaSourcesDecision -> game.submitManaSourcesAutoPay()
                        else -> break
                    }
                }
                game.resolveStack()
            }

            test("each basic land type drives its clause with one of each") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Last Stand")
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val opponentLifeBefore = game.getLifeTotal(2)
                val playerLifeBefore = game.getLifeTotal(1)
                val handBefore = game.state.getHand(game.player1Id).size

                val boltId = findCardInHand(game, 1, "Lightning Bolt")
                val bearsHandId = findCardInHand(game, 1, "Grizzly Bears")

                castLastStand(game, "Grizzly Bears", listOf(boltId, bearsHandId))

                withClue("opponent loses 2 life per Swamp") {
                    game.getLifeTotal(2) shouldBe opponentLifeBefore - 2
                }

                val opponentBear = game.findPermanent("Grizzly Bears")!!
                val damage = game.state.getEntity(opponentBear)?.get<DamageComponent>()?.amount ?: 0
                withClue("one Mountain deals one damage to target creature") {
                    damage shouldBe 1
                }

                withClue("one Forest creates one Saproling token") {
                    saprolingCount(game) shouldBe 1
                }

                withClue("one Plains grants 2 life") {
                    game.getLifeTotal(1) shouldBe playerLifeBefore + 2
                }

                withClue("one Island draws then discards one (net hand unchanged aside from Last Stand)") {
                    game.state.getHand(game.player1Id).size shouldBe handBefore - 1
                }
            }
        }
    }
}
