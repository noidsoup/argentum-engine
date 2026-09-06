package com.wingedsheep.engine.mechanics.foretell

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ForetellCastOptionsComponent
import com.wingedsheep.engine.state.components.identity.PlayWithFixedAlternativeManaCostComponent
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ForetellCostSpec

/**
 * Resolves every foretell cast cost available on a foretold card for [controllerId].
 *
 * Merges the keyword foretell cost ([PlayWithFixedAlternativeManaCostComponent] from
 * [com.wingedsheep.engine.handlers.actions.ability.ForetellCardHandler]) with any additional
 * options in [ForetellCastOptionsComponent] (from [MakeForetoldEffect]). Distinct costs are
 * returned in stable order so enumerator and handler can index them consistently.
 */
object ForetellCastCosts {
    fun allCosts(container: ComponentContainer, controllerId: EntityId): List<ManaCost> {
        val costs = linkedSetOf<ManaCost>()
        container.get<PlayWithFixedAlternativeManaCostComponent>()
            ?.takeIf { it.controllerId == controllerId }
            ?.fixedCost
            ?.let(costs::add)
        container.get<ForetellCastOptionsComponent>()
            ?.takeIf { it.controllerId == controllerId }
            ?.costs
            ?.forEach(costs::add)
        return costs.toList()
    }

    fun resolveCost(
        container: ComponentContainer,
        controllerId: EntityId,
        foretellCostIndex: Int?,
    ): ManaCost? {
        val costs = allCosts(container, controllerId)
        if (costs.isEmpty()) return null
        val index = foretellCostIndex ?: 0
        return costs.getOrNull(index)
    }

    fun computeForetellCost(
        cardRegistry: CardRegistry,
        cardComponent: CardComponent,
        spec: ForetellCostSpec,
    ): ManaCost {
        val manaCost = cardRegistry.getCard(cardComponent.cardDefinitionId)?.manaCost
            ?: cardComponent.manaCost
        return when (spec) {
            is ForetellCostSpec.Fixed -> spec.cost
            is ForetellCostSpec.ManaCostReducedByGeneric -> manaCost.reduceGeneric(spec.amount)
        }
    }

    fun keywordForetellCost(cardRegistry: CardRegistry, cardComponent: CardComponent): ManaCost? {
        val def = cardRegistry.getCard(cardComponent.cardDefinitionId) ?: return null
        return def.keywordAbilities.filterIsInstance<KeywordAbility.Foretell>().firstOrNull()?.cost
    }
}
