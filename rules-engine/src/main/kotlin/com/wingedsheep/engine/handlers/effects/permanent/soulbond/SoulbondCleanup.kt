package com.wingedsheep.engine.handlers.effects.permanent.soulbond

import com.wingedsheep.engine.core.CreaturesUnpairedEvent
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.SoulbondPairComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.model.EntityId

/**
 * Clear a Soulbond pair involving [permanentId] (CR 702.95e).
 *
 * Strips [SoulbondPairComponent] from [permanentId] and, when still present, from its mate.
 * Returns the updated state plus an optional [CreaturesUnpairedEvent].
 */
internal fun clearSoulbondPair(
    state: GameState,
    permanentId: EntityId,
): Pair<GameState, List<GameEvent>> {
    val pair = state.getEntity(permanentId)?.get<SoulbondPairComponent>() ?: return state to emptyList()
    val partnerId = pair.partnerId

    var newState = state.updateEntity(permanentId) { it.without<SoulbondPairComponent>() }
    val partnerStillPairedToUs = newState.getEntity(partnerId)
        ?.get<SoulbondPairComponent>()
        ?.partnerId == permanentId
    if (partnerStillPairedToUs) {
        newState = newState.updateEntity(partnerId) { it.without<SoulbondPairComponent>() }
    }

    val firstName = state.getEntity(permanentId)?.get<CardComponent>()?.name ?: "Creature"
    val secondName = state.getEntity(partnerId)?.get<CardComponent>()?.name ?: "Creature"
    return newState to listOf(
        CreaturesUnpairedEvent(
            firstId = permanentId,
            firstName = firstName,
            secondId = partnerId,
            secondName = secondName,
        )
    )
}

/**
 * CR 702.95e — unpair when another player gains control of either half of a soulbond pair.
 *
 * Call from every executor that emits [com.wingedsheep.engine.core.ControlChangedEvent] (same call
 * sites as [com.wingedsheep.engine.handlers.effects.permanent.control.clearRingBearerOnControlChange]).
 * Those paths only run when control actually moves, so simply clearing any pair on [permanentId]
 * is correct.
 */
internal fun clearSoulbondOnControlChange(
    state: GameState,
    permanentId: EntityId,
): GameState {
    if (state.getEntity(permanentId)?.get<SoulbondPairComponent>() == null) return state
    return clearSoulbondPair(state, permanentId).first
}
