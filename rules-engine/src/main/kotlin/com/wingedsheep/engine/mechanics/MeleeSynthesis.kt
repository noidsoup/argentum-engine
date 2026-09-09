package com.wingedsheep.engine.mechanics

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.Melee
import com.wingedsheep.sdk.scripting.filters.unified.Scope
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate
import com.wingedsheep.sdk.scripting.predicates.evaluateWith

/**
 * Derives how many melee instances a battlefield permanent carries (CR 702.121b) and builds the
 * synthesized attack triggers. Printed instances come from [Melee.printedInstanceCount]; each
 * battlefield [GrantKeyword] for [Keyword.MELEE] whose filter matches the permanent adds another.
 */
object MeleeSynthesis {

    fun instanceCount(
        entityId: EntityId,
        cardDefinitionId: String,
        state: GameState,
        cardRegistry: CardRegistry,
    ): Int {
        if (!state.projectedState.hasKeyword(entityId, Keyword.MELEE)) return 0
        val cardDef = cardRegistry.getCard(cardDefinitionId) ?: return 0
        val printed = Melee.printedInstanceCount(cardDef)
        val granted = countGrantedMeleeInstances(entityId, state, cardRegistry)
        return (printed + granted).coerceAtLeast(1)
    }

    private fun countGrantedMeleeInstances(
        entityId: EntityId,
        state: GameState,
        cardRegistry: CardRegistry,
    ): Int {
        val targetContainer = state.getEntity(entityId) ?: return 0
        if (targetContainer.has<FaceDownComponent>()) return 0
        val targetCard = targetContainer.get<CardComponent>() ?: return 0
        val projected = state.projectedState
        val targetControllerId = projected.getController(entityId)
        var count = 0

        for (permanentId in state.getBattlefield()) {
            val container = state.getEntity(permanentId) ?: continue
            if (container.has<FaceDownComponent>()) continue
            val sourceControllerId = projected.getController(permanentId) ?: continue
            val sourceCard = container.get<CardComponent>() ?: continue
            val cardDef = cardRegistry.getCard(sourceCard.cardDefinitionId) ?: continue
            for (ability in cardDef.staticAbilities) {
                if (ability !is GrantKeyword || ability.keyword != Keyword.MELEE.name) continue
                if (ability.filter.excludeSelf && permanentId == entityId) continue
                if (ability.filter.scope !is Scope.Battlefield) continue
                val filter = ability.filter.baseFilter
                val matchesAll = filter.cardPredicates.all { predicate ->
                    when (predicate) {
                        is com.wingedsheep.sdk.scripting.predicates.CardPredicate.IsCreature ->
                            projected.isCreature(entityId)
                        is com.wingedsheep.sdk.scripting.predicates.CardPredicate.HasSubtype ->
                            targetCard.typeLine.hasSubtype(predicate.subtype)
                        else -> true
                    }
                }
                if (!matchesAll) continue
                val controllerMatch = filter.controllerPredicate?.evaluateWith { leaf ->
                    when (leaf) {
                        is ControllerPredicate.ControlledByYou ->
                            targetControllerId == sourceControllerId
                        is ControllerPredicate.ControlledByOpponent ->
                            targetControllerId != null && targetControllerId != sourceControllerId
                        else -> null
                    }
                } ?: true
                if (controllerMatch) count++
            }
        }
        return count
    }
}
