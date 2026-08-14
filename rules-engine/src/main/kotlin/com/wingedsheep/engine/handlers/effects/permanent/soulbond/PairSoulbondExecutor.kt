package com.wingedsheep.engine.handlers.effects.permanent.soulbond

import com.wingedsheep.engine.core.CreaturesPairedEvent
import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.SoulbondPairComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.PairWithSoulbondEffect
import kotlin.reflect.KClass

/**
 * Executor for [PairWithSoulbondEffect] (CR 702.95).
 *
 * On resolution rechecks CR 702.95c/d: both objects must still be creatures on the battlefield
 * under the ability's controller, and both must be unpaired (or already paired *to each other*).
 * Then writes [SoulbondPairComponent] symmetrically and emits [CreaturesPairedEvent].
 */
class PairSoulbondExecutor : EffectExecutor<PairWithSoulbondEffect> {

    override val effectType: KClass<PairWithSoulbondEffect> = PairWithSoulbondEffect::class

    override fun execute(
        state: GameState,
        effect: PairWithSoulbondEffect,
        context: EffectContext
    ): EffectResult {
        val sourceId = context.sourceId
            ?: return EffectResult.success(state)
        val partnerId = context.resolveTarget(effect.target, state)
            ?: return EffectResult.success(state)
        if (sourceId == partnerId) return EffectResult.success(state)

        val controllerId = context.controllerId
        if (!canPair(state, sourceId, partnerId, controllerId)) {
            return EffectResult.success(state)
        }

        var newState = state
            .updateEntity(sourceId) { it.with(SoulbondPairComponent(partnerId)) }
            .updateEntity(partnerId) { it.with(SoulbondPairComponent(sourceId)) }

        val sourceName = newState.getEntity(sourceId)?.get<CardComponent>()?.name ?: "Creature"
        val partnerName = newState.getEntity(partnerId)?.get<CardComponent>()?.name ?: "Creature"

        return EffectResult.success(
            newState,
            listOf(
                CreaturesPairedEvent(
                    firstId = sourceId,
                    firstName = sourceName,
                    secondId = partnerId,
                    secondName = partnerName,
                    controllerId = controllerId,
                )
            )
        )
    }

    companion object {
        /**
         * CR 702.95c/d legality: both still creatures on BF under [controllerId], both unpaired
         * (or already paired to each other).
         */
        fun canPair(
            state: GameState,
            a: EntityId,
            b: EntityId,
            controllerId: EntityId,
        ): Boolean {
            val projected = state.projectedState
            if (a !in state.getBattlefield() || b !in state.getBattlefield()) return false
            if (!projected.isCreature(a) || !projected.isCreature(b)) return false
            if (projected.getController(a) != controllerId) return false
            if (projected.getController(b) != controllerId) return false

            val aPair = state.getEntity(a)?.get<SoulbondPairComponent>()
            val bPair = state.getEntity(b)?.get<SoulbondPairComponent>()
            // Already paired to each other — allow (idempotent).
            if (aPair?.partnerId == b && bPair?.partnerId == a) return true
            // Either already paired to someone else — fail (CR 702.95d).
            if (aPair != null || bPair != null) return false
            return true
        }
    }
}
