package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Armored Griffin
 * {3}{W}
 * Creature — Griffin
 * 2/3
 *
 * Flying, vigilance
 *
 * Printed as "Flying, attacking doesn't cause this creature to tap"; the modern Oracle text uses
 * the vigilance keyword, which is what's modeled here.
 */
val ArmoredGriffin = card("Armored Griffin") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    power = 2
    toughness = 3
    oracleText = "Flying, vigilance"

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "13"
        artist = "Bradley Williams"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/54866603-c80c-4dc8-9655-eaf54ed2c5ab.jpg?1783946495"
    }
}
