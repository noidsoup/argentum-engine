package com.wingedsheep.engine.handlers.effects.library

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.DynamicAmountEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.scripting.effects.RecordPerPlayerNumberEffect
import kotlin.reflect.KClass

/**
 * Executor for [RecordPerPlayerNumberEffect].
 *
 * Evaluates [amount] for the current [EffectContext.controllerId] and merges it into
 * pipeline `storedPerPlayerNumbers[storeAs]`.
 */
class RecordPerPlayerNumberExecutor(
    private val amountEvaluator: DynamicAmountEvaluator = DynamicAmountEvaluator(),
) : EffectExecutor<RecordPerPlayerNumberEffect> {

    override val effectType: KClass<RecordPerPlayerNumberEffect> = RecordPerPlayerNumberEffect::class

    override fun execute(
        state: GameState,
        effect: RecordPerPlayerNumberEffect,
        context: EffectContext,
    ): EffectResult {
        val value = amountEvaluator.evaluate(state, effect.amount, context)
        return EffectResult(
            state = state,
            updatedStoredPerPlayerNumbers = mapOf(
                effect.storeAs to mapOf(context.controllerId to value),
            ),
        )
    }
}
