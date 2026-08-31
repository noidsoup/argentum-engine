package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Earthshaking Si — Global Series: Jiang Yanggu & Mu Yanling #31
 * {5}{G} · Creature — Beast · 5/5
 *
 * Trample
 */
val EarthshakingSi = card("Earthshaking Si") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 5
    oracleText = "Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31"
        artist = "Shinchuen Chen"
        flavorText =
            "\"For reasons unknown to me, herds in the Arrow Bamboo Forest have become more and more agitated.\"\n—Jiang Yanggu's travelogue"
        imageUri = "https://cards.scryfall.io/normal/front/4/1/418df457-4aab-486c-b691-41f03ec8a6df.jpg?1783934625"
    }
}
