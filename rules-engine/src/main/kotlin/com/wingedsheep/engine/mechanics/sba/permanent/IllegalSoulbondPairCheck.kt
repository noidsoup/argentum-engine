package com.wingedsheep.engine.mechanics.sba.permanent

import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.handlers.effects.permanent.soulbond.clearSoulbondPair
import com.wingedsheep.engine.mechanics.sba.SbaOrder
import com.wingedsheep.engine.mechanics.sba.StateBasedActionCheck
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.SoulbondPairComponent

/**
 * CR 702.95e — a paired creature becomes unpaired when:
 * - another player gains control of either half (projected controllers differ),
 * - either stops being a creature, or
 * - either leaves the battlefield (partner missing / already cleared on leave).
 *
 * Not listed under 704, but continuous legality (same spirit as Equipment host checks in
 * [UnattachedAurasCheck]). Reads **projected** creature-ness and controllers so Layer-2 /
 * Layer-4 continuous effects are seen (ControlEnchantedPermanent, Humility, etc.).
 */
class IllegalSoulbondPairCheck : StateBasedActionCheck {
    override val name = "702.95e Illegal Soulbond pair"
    override val order = SbaOrder.ILLEGAL_SOULBOND_PAIR

    override fun check(state: GameState): ExecutionResult {
        var newState = state
        val events = mutableListOf<com.wingedsheep.engine.core.GameEvent>()
        val projected = state.projectedState
        val seen = mutableSetOf<com.wingedsheep.sdk.model.EntityId>()

        for (entityId in state.getBattlefield().toList()) {
            if (entityId in seen) continue
            val pair = state.getEntity(entityId)?.get<SoulbondPairComponent>() ?: continue
            seen.add(entityId)
            seen.add(pair.partnerId)

            val aIsCreature = projected.isCreature(entityId)
            val bOnBattlefield = pair.partnerId in state.getBattlefield()
            val bIsCreature = bOnBattlefield && projected.isCreature(pair.partnerId)
            val sameController = bOnBattlefield &&
                projected.getController(entityId) != null &&
                projected.getController(entityId) == projected.getController(pair.partnerId)
            if (aIsCreature && bIsCreature && sameController) continue

            val (cleared, clearedEvents) = clearSoulbondPair(newState, entityId)
            newState = cleared
            events.addAll(clearedEvents)
        }

        return if (events.isEmpty() && newState === state) {
            ExecutionResult.success(state)
        } else {
            ExecutionResult.success(newState, events)
        }
    }
}
