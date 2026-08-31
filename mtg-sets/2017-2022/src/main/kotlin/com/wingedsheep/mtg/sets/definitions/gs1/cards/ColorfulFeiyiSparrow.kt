package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Colorful Feiyi Sparrow — Global Series: Jiang Yanggu & Mu Yanling #2
 * {1}{W} · Creature — Bird · 1/3
 *
 * Flying
 */
val ColorfulFeiyiSparrow = card("Colorful Feiyi Sparrow") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird"
    power = 1
    toughness = 3
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "Kee Lo"
        flavorText = "These nimble sparrows, not often seen, delight in tricking the pangolins that wander the forest."
        imageUri = "https://cards.scryfall.io/normal/front/8/4/846169fb-8f63-4a0f-af94-e08af8927144.jpg?1783934637"
    }
}
