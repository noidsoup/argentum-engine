package com.wingedsheep.engine.event

import com.wingedsheep.engine.mechanics.layers.ProjectedState
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.PairWithSourceEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect

/**
 * Drops printed triggered abilities whose keyword they *represent* is absent from the permanent's
 * projected characteristics (CR 702.95 — Soulbond is a keyword that represents two triggered
 * abilities). A copy that "loses soulbond" strips [Keyword.SOULBOND] from its copiable keywords via
 * [com.wingedsheep.sdk.scripting.effects.CopyExceptions.removedKeywords]; without this pass the
 * card-definition pairing triggers would still fire.
 */
object KeywordRepresentativeTriggerFilter {

    fun filter(
        abilities: List<TriggeredAbility>,
        entityId: EntityId,
        projected: ProjectedState,
    ): List<TriggeredAbility> {
        if (abilities.isEmpty()) return abilities
        var result = abilities
        if (!projected.hasKeyword(entityId, Keyword.SOULBOND)) {
            result = result.filterNot { representsSoulbond(it) }
        }
        return result
    }

    private fun representsSoulbond(ability: TriggeredAbility): Boolean =
        effectContains(ability.effect, PairWithSourceEffect::class)

    private fun effectContains(effect: Effect, type: kotlin.reflect.KClass<out Effect>): Boolean =
        when {
            type.isInstance(effect) -> true
            effect is CompositeEffect -> effect.effects.any { effectContains(it, type) }
            effect is GatedEffect ->
                effectContains(effect.then, type) ||
                    effect.otherwise?.let { effectContains(it, type) } == true ||
                    when (val gate = effect.gate) {
                        is Gate.DoAction -> effectContains(gate.action, type)
                        is Gate.MayPay -> effectContains(gate.cost, type)
                        else -> false
                    }
            effect is ReflexiveTriggerEffect ->
                effectContains(effect.action, type) ||
                    effect.reflexiveEffect?.let { effectContains(it, type) } == true
            else -> false
        }
}
