package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Dearly Departed
 * {4}{W}{W}
 * Creature — Spirit
 * 5/5
 * Flying
 * As long as this creature is in your graveyard, each Human creature you control enters with an
 * additional +1/+1 counter on it.
 */
val DearlyDeparted = card("Dearly Departed") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    oracleText =
        "Flying\n" +
            "As long as this creature is in your graveyard, each Human creature you control enters " +
            "with an additional +1/+1 counter on it."
    power = 5
    toughness = 5

    keywords(Keyword.FLYING)

    replacementEffect(
        EntersWithCounters(
            count = 1,
            selfOnly = false,
            activeFromZones = setOf(Zone.GRAVEYARD),
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Creature.withSubtype(Subtype.HUMAN).youControl(),
                to = Zone.BATTLEFIELD,
            ),
        ),
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "9"
        artist = "Daniel Ljunggren"
        flavorText = "\"Never forget our ancestors. They have not forgotten us.\"\n—Mikaeus, the Lunarch"
        imageUri =
            "https://cards.scryfall.io/normal/front/d/0/d008061f-cda4-4bcf-b6b3-d1b4a251cc66.jpg?1783940996"
    }
}
