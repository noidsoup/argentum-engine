package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Feiyi Snake — Global Series: Jiang Yanggu & Mu Yanling #24
 * {1}{G} · Creature — Snake · 2/1
 *
 * Reach
 */
val FeiyiSnake = card("Feiyi Snake") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Snake"
    power = 2
    toughness = 1
    oracleText = "Reach (This creature can block creatures with flying.)"

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Qiu De En"
        flavorText = "Two bodies, twice the malice."
        imageUri = "https://cards.scryfall.io/normal/front/0/4/0410420a-c093-4540-8867-28d0f2d86b56.jpg?1783934627"
    }
}
