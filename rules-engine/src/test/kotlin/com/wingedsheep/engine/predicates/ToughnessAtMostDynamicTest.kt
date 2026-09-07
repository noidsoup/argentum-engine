package com.wingedsheep.engine.predicates

import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Engine wiring for [CardPredicate.ToughnessAtMostDynamic] — the open-ended "toughness X or
 * less, where X is …" cap whose X is **not** the spell's chosen {X}.
 *
 * Spectral Deluge: "Return each creature your opponents control with toughness X or less to its
 * owner's hand, where X is the number of Islands you control."
 */
class ToughnessAtMostDynamicTest : FunSpec({

    val evaluator = PredicateEvaluator()
    val controller = EntityId.generate()
    val opponent = EntityId.generate()

    val islandsYouControl = DynamicAmounts.battlefield(
        Player.You,
        GameObjectFilter.Land.withSubtype(Subtype.ISLAND),
    ).count()

    fun spectralDelugeCapFilter() = GameObjectFilter.Creature.toughnessAtMostDynamic(islandsYouControl)

    fun island(): ComponentContainer =
        ComponentContainer()
            .with(
                CardComponent(
                    cardDefinitionId = "Island",
                    name = "Island",
                    manaCost = ManaCost.parse(""),
                    typeLine = TypeLine(cardTypes = setOf(CardType.LAND), subtypes = setOf(Subtype.ISLAND)),
                    ownerId = controller,
                )
            )
            .with(OwnerComponent(controller))
            .with(ControllerComponent(controller))

    fun creature(
        name: String,
        toughness: Int,
        owner: EntityId,
        controllerId: EntityId,
        plusOnes: Int = 0,
    ): Pair<EntityId, ComponentContainer> {
        val id = EntityId.generate()
        var container = ComponentContainer()
            .with(
                CardComponent(
                    cardDefinitionId = name,
                    name = name,
                    manaCost = ManaCost.parse("{1}"),
                    typeLine = TypeLine.creature(),
                    baseStats = CreatureStats(1, toughness),
                    ownerId = owner,
                )
            )
            .with(OwnerComponent(owner))
            .with(ControllerComponent(controllerId))
        if (plusOnes > 0) {
            container = container.with(
                CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to plusOnes))
            )
        }
        return id to container
    }

    fun board(
        islandCount: Int,
        opponentCreatures: List<Triple<String, Int, Int>> = emptyList(),
    ): GameState {
        var state = GameState()
            .withEntity(controller, ComponentContainer())
            .withEntity(opponent, ComponentContainer())

        repeat(islandCount) { index ->
            val id = EntityId.generate()
            state = state.withEntity(id, island())
                .addToZone(ZoneKey(controller, Zone.BATTLEFIELD), id)
        }

        opponentCreatures.forEach { (name, toughness, plusOnes) ->
            val (id, container) = creature(name, toughness, opponent, opponent, plusOnes)
            state = state.withEntity(id, container)
                .addToZone(ZoneKey(opponent, Zone.BATTLEFIELD), id)
        }
        return state
    }

    fun GameState.matchesCreature(name: String, filter: GameObjectFilter, context: PredicateContext?): Boolean {
        val id = getBattlefield()
            .mapNotNull { getEntity(it)?.get<CardComponent>()?.let { card -> it to card.name } }
            .first { (_, n) -> n == name }
            .first
        return evaluator.matches(this, projectedState, id, filter, context ?: PredicateContext(controllerId = controller))
    }

    fun contextForController() = PredicateContext(controllerId = controller)

    test("creatures at or below the island count match; higher toughness does not") {
        val state = board(
            islandCount = 3,
            opponentCreatures = listOf(
                Triple("Small", 2, 0),
                Triple("Exact", 3, 0),
                Triple("Large", 4, 0),
            ),
        )
        val filter = spectralDelugeCapFilter()
        val ctx = contextForController()

        withClue("toughness 2 with X = 3 islands") {
            state.matchesCreature("Small", filter, ctx) shouldBe true
        }
        withClue("toughness 3 with X = 3 islands") {
            state.matchesCreature("Exact", filter, ctx) shouldBe true
        }
        withClue("toughness 4 with X = 3 islands") {
            state.matchesCreature("Large", filter, ctx) shouldBe false
        }
    }

    test("zero islands — only toughness 0 creatures match") {
        val state = board(
            islandCount = 0,
            opponentCreatures = listOf(
                Triple("Zero", 0, 0),
                Triple("One", 1, 0),
            ),
        )
        val filter = spectralDelugeCapFilter()
        val ctx = contextForController()

        state.matchesCreature("Zero", filter, ctx) shouldBe true
        state.matchesCreature("One", filter, ctx) shouldBe false
    }

    test("reads projected toughness, not printed base") {
        val state = board(
            islandCount = 2,
            opponentCreatures = listOf(
                Triple("Pumped", 2, 1),
            ),
        )
        val filter = spectralDelugeCapFilter()
        val ctx = contextForController()

        withClue("printed 2/2 with a +1/+1 counter is toughness 3, above X = 2") {
            state.matchesCreature("Pumped", filter, ctx) shouldBe false
        }
    }

    test("is independent of spell X — ToughnessAtMostX reads xValue, not island count") {
        val state = board(
            islandCount = 1,
            opponentCreatures = listOf(
                Triple("Two", 2, 0),
                Triple("One", 1, 0),
            ),
        )
        val spellXFilter = GameObjectFilter.Creature.toughnessAtMostX()
        val ctxWithHighSpellX = PredicateContext(controllerId = controller, xValue = 5)
        val ctxWithLowSpellX = PredicateContext(controllerId = controller, xValue = 1)

        withClue("spell X = 5 lets toughness 2 through") {
            state.matchesCreature("Two", spellXFilter, ctxWithHighSpellX) shouldBe true
        }
        withClue("dynamic cap X = 1 islands excludes toughness 2") {
            state.matchesCreature("Two", spectralDelugeCapFilter(), ctxWithLowSpellX) shouldBe false
        }
        withClue("spell X = 1 excludes toughness 2 even with many islands") {
            state.matchesCreature("Two", spellXFilter, ctxWithLowSpellX) shouldBe false
        }
    }

    test("non-island lands do not increase X") {
        val mountainId = EntityId.generate()
        var state = board(islandCount = 1, opponentCreatures = listOf(Triple("Borderline", 2, 0)))
        state = state.withEntity(
            mountainId,
            ComponentContainer()
                .with(
                    CardComponent(
                        cardDefinitionId = "Mountain",
                        name = "Mountain",
                        manaCost = ManaCost.parse(""),
                        typeLine = TypeLine(cardTypes = setOf(CardType.LAND), subtypes = setOf(Subtype.MOUNTAIN)),
                        ownerId = controller,
                    )
                )
                .with(OwnerComponent(controller))
                .with(ControllerComponent(controller)),
        ).addToZone(ZoneKey(controller, Zone.BATTLEFIELD), mountainId)

        val filter = spectralDelugeCapFilter()
        val ctx = contextForController()

        withClue("one Island and one Mountain — X is 1, not 2") {
            state.matchesCreature("Borderline", filter, ctx) shouldBe false
        }
    }
})
