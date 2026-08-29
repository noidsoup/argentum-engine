package com.wingedsheep.engine.handlers.effects.zones

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.TargetFinder
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.TargetResolutionUtils
import com.wingedsheep.engine.handlers.effects.library.MoveCollectionExecutor
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.scripting.effects.PutOntoBattlefieldAttachedToEffect
import kotlin.reflect.KClass

/**
 * Executor for [PutOntoBattlefieldAttachedToEffect].
 *
 * Moves the first card in a pipeline collection onto the battlefield attached to a fixed host.
 * Aura-only; if the host is gone or the Aura can't legally enchant it, the Aura stays put
 * (Rule 303.4g).
 */
class PutOntoBattlefieldAttachedToExecutor(
    private val cardRegistry: CardRegistry,
    private val targetFinder: TargetFinder,
) : EffectExecutor<PutOntoBattlefieldAttachedToEffect> {

    private val moveCollectionExecutor = MoveCollectionExecutor(cardRegistry, targetFinder)

    override val effectType: KClass<PutOntoBattlefieldAttachedToEffect> =
        PutOntoBattlefieldAttachedToEffect::class

    override fun execute(
        state: GameState,
        effect: PutOntoBattlefieldAttachedToEffect,
        context: EffectContext,
    ): EffectResult {
        val auraId = context.pipeline.storedCollections[effect.from]?.firstOrNull()
            ?: return EffectResult.success(state)

        val hostId = TargetResolutionUtils.resolveTarget(effect.host, context, state)
            ?: return EffectResult.success(state)

        if (!state.getBattlefield().contains(hostId)) {
            return EffectResult.success(state)
        }

        val cardComponent = state.getEntity(auraId)?.get<CardComponent>() ?: return EffectResult.success(state)
        if (!cardComponent.typeLine.isAura) {
            return EffectResult.success(state)
        }

        val auraTarget = cardRegistry.getCard(cardComponent.cardDefinitionId)?.script?.auraTarget
            ?: return EffectResult.success(state)

        val legalHosts = targetFinder.findLegalTargets(
            state = state,
            requirement = auraTarget,
            controllerId = context.controllerId,
            sourceId = auraId,
            ignoreTargetingRestrictions = true,
        )
        if (hostId !in legalHosts) {
            return EffectResult.success(state)
        }

        val (newState, events) = moveCollectionExecutor.moveAuraToBattlefield(
            state = state,
            auraId = auraId,
            targetId = hostId,
            destPlayerId = context.controllerId,
        )
        return EffectResult.success(newState, events)
    }
}
