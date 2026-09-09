package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.Melee
import com.wingedsheep.sdk.scripting.TriggeredAbility

/**
 * The triggered ability that *is* Melee (CR 702.121a). Exposed standalone so tokens and granted
 * shells can reuse the same shape as [melee].
 */
fun meleeTriggeredAbility(): TriggeredAbility = Melee.attackTrigger()

/**
 * Add Melee (CR 702.121) — keyword + engine-synthesized attack trigger.
 *
 * The keyword is display-only on the card; the behavior is derived at runtime from projected
 * [Keyword.MELEE] (intrinsic or granted). Call [meleeTriggeredAbility] only when authoring a
 * token that must carry the ability text without relying on synthesis.
 */
fun CardBuilder.melee() {
    keywordSet.add(Keyword.MELEE)
    keywordAbilityList.add(KeywordAbility.Simple(Keyword.MELEE))
}
