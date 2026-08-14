package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Niambi, Faithful Healer
 * {1}{W}{U}
 * Legendary Creature — Human Cleric
 * 2/2
 *
 * When Niambi enters, you may search your library and/or graveyard for a card named
 * Teferi, Timebender, reveal it, and put it into your hand. If you search your library
 * this way, shuffle.
 */
val NiambiFaithfulHealer = card("Niambi, Faithful Healer") {
    manaCost = "{1}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "When Niambi enters, you may search your library and/or graveyard for a card " +
        "named Teferi, Timebender, reveal it, and put it into your hand. If you search your " +
        "library this way, shuffle."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchMultipleZones(
            zones = listOf(Zone.LIBRARY, Zone.GRAVEYARD),
            filter = GameObjectFilter.Any.named("Teferi, Timebender"),
            count = 1,
            destination = SearchDestination.HAND,
            reveal = true,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "272"
        artist = "Greg Opalinski"
        flavorText = "\"My father will be happy to see you.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c638677c-2b92-4d0c-b61c-598b5a843844.jpg?1783934937"
    }
}
