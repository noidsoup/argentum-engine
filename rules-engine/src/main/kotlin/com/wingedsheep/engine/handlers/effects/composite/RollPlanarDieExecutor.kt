package com.wingedsheep.engine.handlers.effects.composite

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.core.PlanarDieFace
import com.wingedsheep.engine.core.PlanarDieRolledEvent
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.scripting.effects.RollPlanarDieEffect
import kotlin.reflect.KClass

/**
 * Executor for [RollPlanarDieEffect].
 *
 * Rolls the planar die: 4/6 blank, 1/6 chaos, 1/6 planeswalk (CR Planechase planar die).
 */
class RollPlanarDieExecutor : EffectExecutor<RollPlanarDieEffect> {

    override val effectType: KClass<RollPlanarDieEffect> = RollPlanarDieEffect::class

    override fun execute(
        state: GameState,
        effect: RollPlanarDieEffect,
        context: EffectContext,
    ): EffectResult {
        val sourceId = context.sourceId
        val sourceName = sourceId?.let { state.getEntity(it)?.get<CardComponent>()?.name } ?: "Unknown"

        val (roll, advanced) = state.nextRandom { nextInt(6) }
        val face = when (roll) {
            0, 1, 2, 3 -> PlanarDieFace.BLANK
            4 -> PlanarDieFace.CHAOS
            else -> PlanarDieFace.PLANESWALK
        }

        val events = listOf<GameEvent>(
            PlanarDieRolledEvent(
                playerId = context.controllerId,
                result = face,
                sourceId = sourceId ?: context.controllerId,
                sourceName = sourceName,
            ),
        )

        return EffectResult(state = advanced, events = events)
    }
}
