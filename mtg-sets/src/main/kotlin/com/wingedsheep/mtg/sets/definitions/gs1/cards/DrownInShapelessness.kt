package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Drown in Shapelessness — Global Series: Jiang Yanggu & Mu Yanling #15
 * {1}{U} · Instant
 *
 * Return target creature to its owner's hand.
 */
val DrownInShapelessness = card("Drown in Shapelessness") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target creature to its owner's hand."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.Move(t, Zone.HAND)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Pon Lee"
        flavorText =
            "\"From the swallows, I learned how to ride the winds to anywhere, and from my tears, I mastered how to harness the water.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31420a65-5f13-4ae9-8d45-2531ee2771e8.jpg?1783934631"
    }
}
