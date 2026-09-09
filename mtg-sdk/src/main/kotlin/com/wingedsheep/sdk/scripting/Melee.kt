package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Melee (CR 702.121) as a composable, content-agnostic primitive.
 *
 * Melee is a keyword ability that represents a triggered ability (CR 702.121a): "Whenever this
 * creature attacks, it gets +1/+1 until end of turn for each opponent you attacked with a
 * creature this combat." Cards carry the bare [Keyword.MELEE] keyword — the engine synthesizes
 * [attackTrigger] for every instance the permanent has (printed or granted), the same way
 * [Flanking] and [Fabricate] are derived.
 *
 * The count of opponents is read at resolution via [DynamicAmount.OpponentsAttackedThisCombat],
 * backed by the attacking player's per-combat `PlayerAttackedPlayersThisCombatComponent` (CR
 * 508.6 — attacking an opponent's planeswalker counts as having attacked that opponent).
 */
object Melee {

    private const val ABILITY_ID_PREFIX = "melee"

    /**
     * One synthesized melee attack trigger. [instance] distinguishes multiple printed or granted
     * instances (CR 702.121b).
     */
    fun attackTrigger(instance: Int = 0): TriggeredAbility {
        val amount = DynamicAmount.OpponentsAttackedThisCombat(Player.You)
        return TriggeredAbility(
            id = AbilityId(if (instance == 0) ABILITY_ID_PREFIX else "${ABILITY_ID_PREFIX}_$instance"),
            trigger = Triggers.Attacks.event,
            binding = Triggers.Attacks.binding,
            effect = ModifyStatsEffect(
                powerModifier = amount,
                toughnessModifier = amount,
                target = EffectTarget.Self,
            ),
            descriptionOverride = "Melee (Whenever this creature attacks, it gets +1/+1 until end of " +
                "turn for each opponent you attacked with a creature this combat.)",
        )
    }

    /**
     * How many melee instances are printed on [cardDef] — not counting battlefield grants.
     *
     * A list length, not a boolean: CR 702.121b makes each instance trigger separately. Prefer
     * [keywordAbilities] entries; fall back to the display [CardDefinition.keywords] set when a
     * card only tags `Keyword.MELEE` there.
     */
    fun printedInstanceCount(cardDef: CardDefinition): Int {
        val fromAbilities = cardDef.keywordAbilities.count {
            it is KeywordAbility.Simple && it.keyword == Keyword.MELEE
        }
        if (fromAbilities > 0) return fromAbilities
        return if (Keyword.MELEE in cardDef.keywords) 1 else 0
    }
}
