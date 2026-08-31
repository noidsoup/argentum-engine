package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stormcloud Spirit — Global Series: Jiang Yanggu & Mu Yanling #11
 * {3}{U}{U} · Creature — Spirit · 4/4
 *
 * Flying
 */
val StormcloudSpirit = card("Stormcloud Spirit") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 4
    toughness = 4
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "11"
        artist = "Kee Lo"
        flavorText =
            "The spirits of all the rivers and oceans came together as an unstoppable force, " +
                "unleashing their fury as lightning and thunder."
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b371bba8-08b1-468b-924b-c1f3d64bb096.jpg?1783934632"
    }
}
