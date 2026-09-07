package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Disorder in the Court
 * {X}{W}{U}
 * Instant
 *
 * Exile X target creatures, then investigate X times. Return the exiled cards to the battlefield
 * tapped under their owners' control at the beginning of the next end step.
 *
 * The exile/return half is the Eerie Interlude blink shape per target, with tapped entry on the
 * delayed return. Investigate follows as a separate effect keyed to the same X paid for the spell.
 */
val DisorderInTheCourt = card("Disorder in the Court") {
    manaCost = "{X}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Instant"
    oracleText = "Exile X target creatures, then investigate X times. Return the exiled cards to " +
        "the battlefield tapped under their owners' control at the beginning of the next end step. " +
        "(To investigate, create a Clue token. It's an artifact with \"{2}, Sacrifice this token: " +
        "Draw a card.\")"

    spell {
        target = TargetCreature(optional = true, dynamicMaxCount = DynamicAmount.XValue)
        effect = ForEachTargetEffect(
            listOf(
                Effects.Move(EffectTarget.ContextTarget(0), Zone.EXILE),
                CreateDelayedTriggerEffect(
                    step = Step.END,
                    effect = Effects.Move(
                        target = EffectTarget.ContextTarget(0),
                        destination = Zone.BATTLEFIELD,
                        placement = ZonePlacement.Tapped,
                    ),
                ),
            ),
        ) then Effects.Investigate(DynamicAmount.XValue)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "29"
        artist = "Zoltan Boros"
        imageUri = "https://cards.scryfall.io/normal/front/5/7/576bee63-54d8-4d5c-ab30-6c9b2a72a501.jpg?1783924997"
    }
}
