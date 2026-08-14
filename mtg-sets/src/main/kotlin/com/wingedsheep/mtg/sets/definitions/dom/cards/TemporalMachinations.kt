package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Temporal Machinations
 * {2}{U}
 * Sorcery
 * Return target creature to its owner's hand. If you control an artifact, draw a card.
 */
val TemporalMachinations = card("Temporal Machinations") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Return target creature to its owner's hand. If you control an artifact, draw a card."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ReturnToHand(creature)
            .then(
                ConditionalEffect(
                    condition = Conditions.ControlArtifact,
                    effect = Effects.DrawCards(1),
                ),
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "271"
        artist = "Zack Stella"
        flavorText = "The Cabal soldier blinked and found day had become night, he had a face full of thistle seeds, and the old man was nowhere to be seen."
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9ba8df17-ab81-4e28-b274-ad38aa2899b3.jpg?1783934938"
    }
}
