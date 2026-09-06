package com.wingedsheep.mtg.sets.definitions.usg.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Windfall
 * {2}{U}
 * Sorcery
 * Each player discards their hand, then draws cards equal to the greatest number of cards a
 * player discarded this way.
 */
val Windfall = card("Windfall") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Each player discards their hand, then draws cards equal to the greatest number " +
        "of cards a player discarded this way."
    spell {
        effect = Patterns.Hand.eachPlayerDiscardsHandDrawsGreatest()
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "111"
        artist = "Pete Venters"
        flavorText = "\"To fill your mind with knowledge, we must start by emptying it.\"\n—Barrin, master wizard"
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2aef4608-5ba8-4636-b5e7-cac57c5c0608.jpg?1783946351"
    }
}
