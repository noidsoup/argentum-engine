package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.DealsDamageEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Farsight Mask
 * {5}
 * Artifact
 *
 * Whenever a source an opponent controls deals damage to you, if this artifact is untapped,
 * you may draw a card.
 */
val FarsightMask = card("Farsight Mask") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a source an opponent controls deals damage to you, if this artifact is " +
        "untapped, you may draw a card."

    triggeredAbility {
        trigger = TriggerSpec(
            DealsDamageEvent(
                recipient = RecipientFilter.You,
                sourceFilter = GameObjectFilter.Any.opponentControls(),
            ),
            TriggerBinding.ANY,
        )
        triggerCondition = Conditions.SourceIsUntapped
        optional = true
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "170"
        artist = "Ben Thompson"
        flavorText = "It turns the adversity of the moment into the knowledge of a lifetime."
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98312bc3-9d2a-480c-bcf0-db8d70d632b9.jpg?1783944521"
    }
}
