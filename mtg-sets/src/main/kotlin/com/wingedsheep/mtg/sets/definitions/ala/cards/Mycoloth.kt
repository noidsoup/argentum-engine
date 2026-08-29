package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDevour
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Mycoloth
 * {3}{G}{G}
 * Creature — Fungus
 * 4/4
 *
 * Devour 2 (As this creature enters, you may sacrifice any number of creatures. It enters with
 * twice that many +1/+1 counters on it.)
 * At the beginning of your upkeep, create a 1/1 green Saproling creature token for each +1/+1
 * counter on this creature.
 */
val Mycoloth = card("Mycoloth") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Fungus"
    oracleText = "Devour 2 (As this creature enters, you may sacrifice any number of creatures. " +
        "It enters with twice that many +1/+1 counters on it.)\n" +
        "At the beginning of your upkeep, create a 1/1 green Saproling creature token for each " +
        "+1/+1 counter on this creature."
    power = 4
    toughness = 4

    keywords(Keyword.DEVOUR)
    keywordAbility(KeywordAbility.devour(2))
    replacementEffect(EntersWithDevour(multiplier = 2))

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = CreateTokenEffect(
            count = DynamicAmounts.countersOnSelf(CounterTypeFilter.PlusOnePlusOne),
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling"),
            imageUri = "https://cards.scryfall.io/normal/front/6/2/622759a9-e68b-48c1-8e03-beaab0a52556.jpg?1783942552",
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "140"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/1/5/15360a16-b785-4ffe-bfa8-6c7f6a37455d.jpg?1783942552"
    }
}
