package com.wingedsheep.engine.mechanics.mana

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.ClassLevelComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ForetellSetupCostGating
import com.wingedsheep.sdk.scripting.ForetellSetupCostTarget
import com.wingedsheep.sdk.scripting.ModifyForetellSetupCost

/**
 * Computes the effective **foretell setup** cost (CR 702.143a — normally {2}) after applying
 * battlefield [ModifyForetellSetupCost] static abilities.
 *
 * Foretell is not a spell, so [CostCalculator] does not touch it; this is foretell's dedicated,
 * parallel cost-reduction path. Both [com.wingedsheep.engine.legalactions.enumerators.ForetellEnumerator]
 * (affordability) and [com.wingedsheep.engine.handlers.actions.ability.ForetellCardHandler]
 * (validate + pay) route their setup cost through here so the two stay in lockstep.
 */
class ForetellSetupCostReducer(
    private val cardRegistry: CardRegistry,
) {
    /** Base setup cost per CR 702.143a. */
    private val baseSetupCost: ManaCost = ManaCost.parse("{2}")

    /**
     * The setup cost [foretellerId] actually pays to foretell from hand, after reductions.
     * Floored at {0}.
     */
    fun effectiveSetupCost(state: GameState, foretellerId: EntityId): ManaCost {
        var totalReduction = 0
        var totalIncrease = 0
        val priorForetells = state.foretellCountThisTurnByPlayer[foretellerId] ?: 0
        for ((sourceId, ability) in scanBattlefield(state)) {
            if (ability.target != ForetellSetupCostTarget.YouForetellFromHand) continue
            if (state.projectedState.getController(sourceId) != foretellerId) continue
            if (!gatingOpen(ability.gating, priorForetells)) continue
            when (val mod = ability.modification) {
                is CostModification.ReduceGeneric -> totalReduction += mod.amount
                is CostModification.IncreaseGeneric -> totalIncrease += mod.amount
                else -> { /* foretell setup only supports flat generic adjustments */ }
            }
        }
        var cost = baseSetupCost
        if (totalIncrease > 0) cost = increaseGeneric(cost, totalIncrease)
        if (totalReduction > 0) cost = cost.reduceGeneric(totalReduction)
        return cost
    }

    private fun gatingOpen(gating: ForetellSetupCostGating, priorForetells: Int): Boolean =
        when (gating) {
            ForetellSetupCostGating.None -> true
            is ForetellSetupCostGating.NthForetellPerTurn -> priorForetells == gating.n - 1
        }

    private fun increaseGeneric(cost: ManaCost, increase: Int): ManaCost {
        if (increase <= 0) return cost
        val colored = cost.symbols.filter { it !is com.wingedsheep.sdk.core.ManaSymbol.Generic }
        val newGeneric = cost.genericAmount + increase
        val symbols = if (newGeneric > 0) {
            listOf(com.wingedsheep.sdk.core.ManaSymbol.Generic(newGeneric)) + colored
        } else colored
        return ManaCost(symbols)
    }

    private fun scanBattlefield(state: GameState): List<Pair<EntityId, ModifyForetellSetupCost>> {
        val results = mutableListOf<Pair<EntityId, ModifyForetellSetupCost>>()
        for (playerId in state.turnOrder) {
            for (entityId in state.getBattlefield(playerId)) {
                val container = state.getEntity(entityId) ?: continue
                val card = container.get<CardComponent>() ?: continue
                val def = cardRegistry.getCard(card.cardDefinitionId) ?: continue
                val classLevel = container.get<ClassLevelComponent>()?.currentLevel
                for (ability in def.script.effectiveStaticAbilities(classLevel)) {
                    if (ability is ModifyForetellSetupCost) results += entityId to ability
                }
            }
        }
        return results
    }
}
