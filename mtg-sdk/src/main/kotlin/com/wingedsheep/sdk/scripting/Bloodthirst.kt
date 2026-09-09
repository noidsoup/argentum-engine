package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.conditions.Condition
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.TurnTracker

/**
 * Bloodthirst N (CR 702.53) as pure data — the static ability every bloodthirst permanent has
 * and none of them prints as a separate line.
 *
 * A card carrying `Bloodthirst 3` shows one keyword line plus its reminder text; the rules give
 * it one ability:
 *
 * **"If an opponent was dealt damage this turn, this permanent enters the battlefield with N
 * +1/+1 counters on it."**
 *
 * The engine synthesizes the [EntersWithCounters] replacement at the entry seam from every
 * bloodthirst instance on the object — printed [KeywordAbility.Numeric], a one-shot grant on the
 * resolving spell ([GrantKeywordToSpellEffect] with a parameter), or a
 * [GrantKeywordToOwnSpells] lord — and evaluates [entryCondition] at the moment the permanent
 * enters. Multiple instances stack (CR 702.53c).
 */
object Bloodthirst {

    /**
     * CR 702.53 — "If an opponent was dealt damage this turn …"
     *
     * Existential over opponents: true when any opponent's damage-received running total this turn
     * is at least 1. Deliberately damage, not life loss (War Elemental's sacrifice gate uses the
     * same tracker; Frilled Sparkshooter's "lost life" gate does not satisfy bloodthirst).
     */
    val entryCondition: Condition = Compare(
        DynamicAmount.TurnTracking(Player.EachOpponent, TurnTracker.DAMAGE_RECEIVED),
        ComparisonOperator.GTE,
        DynamicAmount.Fixed(1),
    )

    /**
     * CR 702.53 — one bloodthirst instance's enters-with replacement.
     *
     * `selfOnly` because bloodthirst applies to the permanent carrying the keyword as it enters,
     * never to other things entering under its controller.
     */
    fun entersWithCounters(n: Int): EntersWithCounters = EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = n,
        selfOnly = true,
        condition = entryCondition,
    )

    /**
     * Every printed `Bloodthirst N` on [cardDef], one [n] per [KeywordAbility.Numeric] instance.
     *
     * Multiple instances each set up their own counters (CR 702.53c), so every matching numeric
     * ability is returned rather than summed — a permanent with bloodthirst 2 and bloodthirst 3
     * yields `[2, 3]`.
     */
    fun printedAmounts(cardDef: CardDefinition): List<Int> =
        cardDef.keywordAbilities
            .filterIsInstance<KeywordAbility.Numeric>()
            .filter { it.keyword == Keyword.BLOODTHIRST }
            .map { it.n }
}
