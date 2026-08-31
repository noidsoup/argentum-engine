package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDevour

/**
 * Thromok the Insatiable
 * {3}{R}{G}
 * Creature — Hellion
 * 1/1
 *
 * Devour X, where X is the number of creatures devoured this way (As this creature enters, you may
 * sacrifice any number of creatures. It enters with X +1/+1 counters on it for each of those
 * creatures.)
 */
val ThromokTheInsatiable = card("Thromok the Insatiable") {
    manaCost = "{3}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Hellion"
    power = 1
    toughness = 1
    oracleText = "Devour X, where X is the number of creatures devoured this way (As this creature enters, you may sacrifice any number of creatures. It enters with X +1/+1 counters on it for each of those creatures.)"

    keywords(Keyword.DEVOUR)
    replacementEffect(EntersWithDevour(multiplier = 1, squareSacrificeCount = true))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "106"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/5/9/59d8d22b-d5e7-412e-86e6-b7cd7c71dbb5.jpg?1783940617"
    }
}
