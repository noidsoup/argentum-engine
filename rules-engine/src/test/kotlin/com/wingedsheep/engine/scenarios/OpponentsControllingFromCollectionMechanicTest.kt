package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsDrawnEvent
import com.wingedsheep.engine.handlers.DynamicAmountEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PipelineState
import com.wingedsheep.engine.handlers.effects.EffectExecutorRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Mechanic tests for [DynamicAmount.OpponentsControllingFromCollection] and
 * [Effects.DrawCardsForEachOpponentControllingFromCollection] — Sudden Salvation's
 * "draw a card for each opponent who controls one or more of those permanents" payoff.
 *
 * Sudden Salvation itself is not authored here; these tests pin the shared primitive.
 */
class OpponentsControllingFromCollectionMechanicTest : FunSpec({

    val SalvationDrawProbe = card("Sudden Salvation Draw Probe") {
        manaCost = "{W}"
        typeLine = "Sorcery"
        oracleText = "You draw a card for each opponent who controls one or more of those permanents."
        spell {
            effect = Effects.DrawCardsForEachOpponentControllingFromCollection("returned")
        }
    }

    val evaluator = DynamicAmountEvaluator()

    fun cardComponent(name: String, ownerId: EntityId) = CardComponent(
        cardDefinitionId = name,
        name = name,
        manaCost = ManaCost(emptyList()),
        typeLine = TypeLine(cardTypes = setOf(CardType.CREATURE)),
        ownerId = ownerId,
    )

    fun battlefieldState(
        controllerId: EntityId,
        opponents: List<EntityId> = emptyList(),
        permanents: List<Triple<EntityId, EntityId, CardComponent>> = emptyList(),
    ): GameState {
        val allPlayers = listOf(controllerId) + opponents
        var state = GameState(
            turnOrder = allPlayers,
            activePlayerId = controllerId,
        )
        allPlayers.forEach { state = state.withEntity(it, ComponentContainer()) }

        for ((cardId, cardControllerId, cardComp) in permanents) {
            val container = ComponentContainer()
                .with(cardComp)
                .with(OwnerComponent(cardComp.ownerId!!))
                .with(ControllerComponent(cardControllerId))
            state = state.withEntity(cardId, container)
            state = state.addToZone(ZoneKey(cardControllerId, Zone.BATTLEFIELD), cardId)
        }
        return state
    }

    fun countOpponentsControlling(
        state: GameState,
        controllerId: EntityId,
        collection: List<EntityId>,
    ): Int = evaluator.evaluate(
        state,
        DynamicAmounts.opponentsControllingFrom("returned"),
        EffectContext(
            sourceId = null,
            controllerId = controllerId,
            pipeline = PipelineState(storedCollections = mapOf("returned" to collection)),
        ),
    )

    test("counts one when a single opponent controls one returned permanent") {
        val controller = EntityId.generate()
        val opponent = EntityId.generate()
        val returned = EntityId.generate()
        val bear = cardComponent("Returned Bear", opponent)

        val state = battlefieldState(
            controllerId = controller,
            opponents = listOf(opponent),
            permanents = listOf(Triple(returned, opponent, bear)),
        )

        countOpponentsControlling(state, controller, listOf(returned)) shouldBe 1
    }

    test("counts zero when only the controller controls the returned permanents") {
        val controller = EntityId.generate()
        val opponent = EntityId.generate()
        val returned = EntityId.generate()
        val bear = cardComponent("Your Bear", controller)

        val state = battlefieldState(
            controllerId = controller,
            opponents = listOf(opponent),
            permanents = listOf(Triple(returned, controller, bear)),
        )

        countOpponentsControlling(state, controller, listOf(returned)) shouldBe 0
    }

    test("counts distinct opponents, not permanents — one opponent controlling two still counts once") {
        val controller = EntityId.generate()
        val opponent = EntityId.generate()
        val first = EntityId.generate()
        val second = EntityId.generate()

        val state = battlefieldState(
            controllerId = controller,
            opponents = listOf(opponent),
            permanents = listOf(
                Triple(first, opponent, cardComponent("Bear A", opponent)),
                Triple(second, opponent, cardComponent("Bear B", opponent)),
            ),
        )

        countOpponentsControlling(state, controller, listOf(first, second)) shouldBe 1
    }

    test("in a three-player pod counts each opponent who controls at least one returned permanent") {
        val controller = EntityId.generate()
        val opponentA = EntityId.generate()
        val opponentB = EntityId.generate()
        val cardA = EntityId.generate()
        val cardB = EntityId.generate()
        val cardC = EntityId.generate()

        val state = battlefieldState(
            controllerId = controller,
            opponents = listOf(opponentA, opponentB),
            permanents = listOf(
                Triple(cardA, opponentA, cardComponent("A's Bear", opponentA)),
                Triple(cardB, opponentB, cardComponent("B's Bear", opponentB)),
                Triple(cardC, controller, cardComponent("Your Bear", controller)),
            ),
        )

        countOpponentsControlling(state, controller, listOf(cardA, cardB, cardC)) shouldBe 2
    }

    test("ignores collection entries that are no longer on the battlefield") {
        val controller = EntityId.generate()
        val opponent = EntityId.generate()
        val onBattlefield = EntityId.generate()
        val inGraveyard = EntityId.generate()
        val graveCard = cardComponent("Dead Bear", opponent)

        var state = battlefieldState(
            controllerId = controller,
            opponents = listOf(opponent),
            permanents = listOf(
                Triple(onBattlefield, opponent, cardComponent("Live Bear", opponent)),
            ),
        )
        state = state
            .withEntity(
                inGraveyard,
                ComponentContainer()
                    .with(graveCard)
                    .with(OwnerComponent(opponent))
                    .with(ControllerComponent(opponent)),
            )
            .addToZone(ZoneKey(opponent, Zone.GRAVEYARD), inGraveyard)

        countOpponentsControlling(state, controller, listOf(onBattlefield, inGraveyard)) shouldBe 1
    }

    test("DrawCardsForEachOpponentControllingFromCollection draws once per qualifying opponent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + SalvationDrawProbe)
        val players = driver.initMultiplayer(
            decks = List(3) { Deck.of("Plains" to 40) },
            skipMulligans = true,
        )
        val active = players[0]
        val opponentA = players[1]
        val opponentB = players[2]

        val returnedA = driver.putCreatureOnBattlefield(opponentA, "Grizzly Bears")
        val returnedB = driver.putCreatureOnBattlefield(opponentB, "Grizzly Bears")
        driver.putCreatureOnBattlefield(active, "Grizzly Bears")

        val handBefore = driver.getHandSize(active)

        val result = EffectExecutorRegistry(cardRegistry = driver.cardRegistry).execute(
            driver.state,
            DrawCardsEffect(DynamicAmounts.opponentsControllingFrom("returned")),
            EffectContext(
                sourceId = null,
                controllerId = active,
                pipeline = PipelineState(
                    storedCollections = mapOf("returned" to listOf(returnedA, returnedB)),
                ),
            ),
        )
        result.error shouldBe null
        driver.replaceState(result.state)

        driver.getHandSize(active) shouldBe handBefore + 2
        result.events.filterIsInstance<CardsDrawnEvent>()
            .single { it.playerId == active }
            .count shouldBe 2
    }

    test("the draw probe draws nothing when no opponent controls a returned permanent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + SalvationDrawProbe)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        val ownReturned = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val handBefore = driver.getHandSize(active)

        val result = EffectExecutorRegistry(cardRegistry = driver.cardRegistry).execute(
            driver.state,
            DrawCardsEffect(DynamicAmounts.opponentsControllingFrom("returned")),
            EffectContext(
                sourceId = null,
                controllerId = active,
                pipeline = PipelineState(storedCollections = mapOf("returned" to listOf(ownReturned))),
            ),
        )
        result.error shouldBe null
        driver.replaceState(result.state)

        driver.getHandSize(active) shouldBe handBefore
        result.events.filterIsInstance<CardsDrawnEvent>().none { it.playerId == active } shouldBe true
    }
})
