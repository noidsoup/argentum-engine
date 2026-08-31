package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Keyword

/**
 * Umbra armor (CR 702.118): if the enchanted creature would be destroyed, instead remove all
 * damage from it and destroy this Aura.
 */
fun CardBuilder.umbraArmor() {
    keywordSet.add(Keyword.UMBRA_ARMOR)
}
