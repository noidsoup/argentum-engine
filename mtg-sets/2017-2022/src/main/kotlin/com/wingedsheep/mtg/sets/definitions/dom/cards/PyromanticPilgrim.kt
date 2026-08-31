package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pyromantic Pilgrim
 * {2}{R}
 * Creature — Human Wizard
 * 3/1
 * Haste (This creature can attack and {T} as soon as it comes under your control.)
 */
val PyromanticPilgrim = card("Pyromantic Pilgrim") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Wizard"
    oracleText = "Haste (This creature can attack and {T} as soon as it comes under your control.)"
    power = 3
    toughness = 1
    keywords(Keyword.HASTE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "278"
        artist = "Magali Villeneuve"
        flavorText = "\"From the volcanoes of Shiv to the Balduvian steppes, I have sought a worthy teacher. At last, I have found one.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f670d02b-9bfc-4671-97ed-59bfbe633d82.jpg?1783934935"
    }
}
