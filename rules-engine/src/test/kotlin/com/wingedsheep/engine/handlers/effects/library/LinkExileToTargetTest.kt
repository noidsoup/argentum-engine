package com.wingedsheep.engine.handlers.effects.library

import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.zones.MoveToZoneEffectExecutor
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.MoveToZoneEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LinkExileToTargetTest : FunSpec({

    val playerId = EntityId.generate()
    val sourceId = EntityId.generate()
    val holderId = EntityId.generate()
    val exiledCard = EntityId.generate()

    fun cardComponent(name: String) = CardComponent(
        cardDefinitionId = name,
        name = name,
        manaCost = ManaCost(emptyList()),
        typeLine = TypeLine(cardTypes = setOf(CardType.CREATURE)),
        ownerId = playerId,
    )

    fun baseState(): GameState {
        var state = GameState().withEntity(playerId, ComponentContainer())
        state = state.withEntity(sourceId, ComponentContainer().with(cardComponent("Source")))
        state = state.withEntity(holderId, ComponentContainer().with(cardComponent("Holder")))
        state = state.withEntity(
            exiledCard,
            ComponentContainer()
                .with(cardComponent("Exiled"))
                .with(OwnerComponent(playerId)),
        )
        state = state.addToZone(ZoneKey(playerId, Zone.GRAVEYARD), exiledCard)
        return state
    }

    test("MoveToZoneEffect linkToTarget links exile to the resolved holder, not the source") {
        var state = baseState()
        val context = EffectContext(
            sourceId = sourceId,
            controllerId = playerId,
            pipeline = com.wingedsheep.engine.handlers.PipelineState(
                storedCollections = mapOf(CREATED_TOKENS to listOf(holderId)),
            ),
        )
        val effect = MoveToZoneEffect(
            target = EffectTarget.SpecificEntity(exiledCard),
            destination = Zone.EXILE,
            fromZone = Zone.GRAVEYARD,
            linkToTarget = EffectTarget.PipelineTarget(CREATED_TOKENS, 0),
        )

        val executor = MoveToZoneEffectExecutor(CardRegistry(), effectExecutor = { _, _, _ ->
            error("unexpected nested effect")
        })
        val result = executor.execute(state, effect, context)

        result.isSuccess shouldBe true
        result.state.getEntity(holderId)?.get<LinkedExileComponent>()?.exiledIds shouldBe listOf(exiledCard)
        result.state.getEntity(sourceId)?.get<LinkedExileComponent>() shouldBe null
        result.state.getZone(ZoneKey(playerId, Zone.EXILE)) shouldBe listOf(exiledCard)
    }
})
