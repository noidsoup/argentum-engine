package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Knight of the White Orchid
 * {W}{W}
 * Creature — Human Knight
 * 2/2
 *
 * First strike
 * When this creature enters, if an opponent controls more lands than you, you may search your
 * library for a Plains card, put it onto the battlefield, then shuffle.
 */
val KnightOfTheWhiteOrchid = card("Knight of the White Orchid") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "First strike\n" +
        "When this creature enters, if an opponent controls more lands than you, you may search " +
        "your library for a Plains card, put it onto the battlefield, then shuffle."

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.OpponentControlsMoreLands
        effect = MayEffect(
            Patterns.Library.searchLibrary(
                filter = GameObjectFilter.Land.withSubtype(Subtype.PLAINS),
                count = 1,
                destination = SearchDestination.BATTLEFIELD,
            )
        )
        description = "When this creature enters, if an opponent controls more lands than you, you " +
            "may search your library for a Plains card, put it onto the battlefield, then shuffle."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "16"
        artist = "Mark Zug"
        flavorText = "Both guide and guard on open plain."
        imageUri = "https://cards.scryfall.io/normal/front/6/4/642c6354-4dab-45c9-a1a6-57bd16f16e30.jpg?1783942580"
    }
}
