package com.wingedsheep.sdk.scripting.conditions

import com.wingedsheep.sdk.scripting.text.TextReplacer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Soulbond (CR 702.95) conditions — intervening-if gates and "while paired" statics.
 */

/**
 * "As long as this creature is paired with another creature" (CR 702.95b).
 * True when the source permanent carries a soulbond pair marker.
 */
@SerialName("SourceIsPaired")
@Serializable
data object SourceIsPaired : Condition {
    override val description: String = "if this creature is paired with another creature"
}

/**
 * Intervening-if for the self-ETB soulbond trigger (CR 702.95a): you control this creature and
 * another creature, and both are unpaired.
 */
@SerialName("CanSoulbondPair")
@Serializable
data object CanSoulbondPair : Condition {
    override val description: String =
        "if you control both this creature and another creature and both are unpaired"
}

/**
 * Intervening-if for the other-creature-ETB soulbond trigger (CR 702.95a): you control both the
 * source and the triggering creature, and both are unpaired.
 */
@SerialName("SourceAndTriggeringBothUnpairedYouControl")
@Serializable
data object SourceAndTriggeringBothUnpairedYouControl : Condition {
    override val description: String =
        "if you control both that creature and this one and both are unpaired"
}
