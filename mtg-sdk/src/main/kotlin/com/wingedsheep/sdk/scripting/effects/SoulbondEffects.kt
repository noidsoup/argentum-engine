package com.wingedsheep.sdk.scripting.effects

import com.wingedsheep.sdk.scripting.targets.EffectTarget
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Pair the source permanent with [target] via Soulbond (CR 702.95).
 *
 * On resolution the executor rechecks CR 702.95c/d (both still creatures on the battlefield under
 * the ability's controller, both unpaired or already paired to each other) and writes a mutual
 * `SoulbondPairComponent` on both entities. Emitting the pair is a no-op when the check fails.
 *
 * @property target the creature to pair with (chosen target for self-ETB; [EffectTarget.TriggeringEntity]
 *   for the other-creature-ETB half).
 */
@SerialName("PairWithSoulbond")
@Serializable
data class PairWithSoulbondEffect(
    val target: EffectTarget = EffectTarget.ContextTarget(0),
) : Effect {
    override val description: String = "Pair this creature with ${target.description}"
}
