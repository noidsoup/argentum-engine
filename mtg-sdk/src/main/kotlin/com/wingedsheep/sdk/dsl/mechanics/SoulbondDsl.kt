package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Reminder text for Soulbond (printed oracle form — shorter than the full CR 702.95a wording).
 */
private const val SOULBOND_REMINDER =
    "Soulbond (You may pair this creature with another unpaired creature when either enters. " +
        "They remain paired for as long as you control both of them.)"

/**
 * Add Soulbond (CR 702.95, Avacyn Restored) — keyword + the two may-pair triggered abilities.
 *
 * > **CR 702.95a** — "Soulbond" means
 * > "When this creature enters, if you control both this creature and another creature and both
 * > are unpaired, you may pair this creature with another unpaired creature you control for as long
 * > as both remain creatures on the battlefield under your control" and
 * > "Whenever another creature you control enters, if you control both that creature and this one
 * > and both are unpaired, you may pair that creature with this creature for as long as both remain
 * > creatures on the battlefield under your control."
 *
 * The keyword is display-only; the behavior is composed here from existing primitives:
 *
 * - **Self ETB** — optional trigger with a target (another unpaired creature you control) whose
 *   effect is [Effects.PairSoulbond]. Intervening-if [Conditions.CanSoulbondPair].
 * - **Other creature you control ETB** — optional trigger with no target; pairs the triggering
 *   permanent with this one via [EffectTarget.TriggeringEntity]. Intervening-if
 *   [Conditions.SourceAndTriggeringBothUnpairedYouControl].
 *
 * Cards then author their own "as long as this creature is paired…" statics gated on
 * [Conditions.SourceIsPaired], granting to [Filters.Self] and/or [Filters.SoulbondPartner].
 */
fun CardBuilder.soulbond() {
    keywordSet.add(Keyword.SOULBOND)

    val unpairedOtherCreatureYouControl = TargetFilter(
        GameObjectFilter.Creature.youControl().unpaired()
    )

    triggeredAbilities.add(
        TriggeredAbility.create(
            trigger = Triggers.EntersBattlefield.event,
            binding = Triggers.EntersBattlefield.binding,
            optional = true,
            targetRequirement = TargetCreature(
                filter = unpairedOtherCreatureYouControl,
            ),
            effect = Effects.PairSoulbond(EffectTarget.ContextTarget(0)),
            triggerCondition = Conditions.CanSoulbondPair,
            descriptionOverride = SOULBOND_REMINDER,
        )
    )

    triggeredAbilities.add(
        TriggeredAbility.create(
            trigger = Triggers.OtherCreatureEnters.event,
            binding = Triggers.OtherCreatureEnters.binding,
            optional = true,
            effect = Effects.PairSoulbond(EffectTarget.TriggeringEntity),
            triggerCondition = Conditions.SourceAndTriggeringBothUnpairedYouControl,
            descriptionOverride = SOULBOND_REMINDER,
        )
    )
}
