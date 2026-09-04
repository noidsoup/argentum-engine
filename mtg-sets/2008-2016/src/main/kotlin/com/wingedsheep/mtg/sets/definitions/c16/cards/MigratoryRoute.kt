package com.wingedsheep.mtg.sets.definitions.c16.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Migratory Route
 * {3}{W}{U}
 * Sorcery
 *
 * Create four 1/1 white Bird creature tokens with flying.
 * Basic landcycling {2} ({2}, Discard this card: Search your library for a basic land card,
 * reveal it, put it into your hand, then shuffle.)
 */
val MigratoryRoute = card("Migratory Route") {
    manaCost = "{3}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Sorcery"
    oracleText = "Create four 1/1 white Bird creature tokens with flying.\n" +
        "Basic landcycling {2} ({2}, Discard this card: Search your library for a basic " +
        "land card, reveal it, put it into your hand, then shuffle.)"

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Bird"),
            keywords = setOf(Keyword.FLYING),
            count = 4,
        )
    }

    keywordAbility(KeywordAbility.basicLandcycling("{2}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "38"
        artist = "Winona Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/9006ff3d-446d-411d-948d-9baee8ea9691.jpg?1783937083"
    }
}
