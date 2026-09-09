package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Add Bloodthirst N (CR 702.53).
 *
 * "If an opponent was dealt damage this turn, this creature enters the battlefield with N
 * +1/+1 counters on it."
 *
 * Wires the display-only keyword from one call:
 *  - [Keyword.BLOODTHIRST] in the base keyword set,
 *  - [KeywordAbility.bloodthirst] for the printed `N` and reminder text, and
 *  - nothing else — the engine synthesizes the enters-with replacement at the entry seam from
 *    the printed `N` (see [com.wingedsheep.sdk.scripting.Bloodthirst]).
 */
fun CardBuilder.bloodthirst(n: Int) {
    keywordSet.add(Keyword.BLOODTHIRST)
    keywordAbility(KeywordAbility.bloodthirst(n))
}
