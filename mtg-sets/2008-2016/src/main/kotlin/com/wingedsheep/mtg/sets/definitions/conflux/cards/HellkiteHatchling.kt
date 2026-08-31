package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDevour
import com.wingedsheep.sdk.scripting.EntersWithKeywords
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Hellkite Hatchling
 * {2}{R}{G}
 * Creature — Dragon
 * 2/2
 *
 * Devour 1 (As this creature enters, you may sacrifice any number of creatures. It enters with
 * that many +1/+1 counters on it.)
 * This creature has flying and trample if it devoured a creature.
 *
 * The conditional keywords are entry-timestamped via [EntersWithKeywords] gated on the
 * +1/+1 counters placed by devour before the permanent finishes entering — not a continuous
 * counter check (CR: whether it devoured is fixed at ETB).
 */
val HellkiteHatchling = card("Hellkite Hatchling") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Dragon"
    oracleText = "Devour 1 (As this creature enters, you may sacrifice any number of creatures. " +
        "It enters with that many +1/+1 counters on it.)\n" +
        "This creature has flying and trample if it devoured a creature."
    power = 2
    toughness = 2

    keywords(Keyword.DEVOUR)
    keywordAbility(KeywordAbility.devour(1))

    replacementEffect(EntersWithDevour(multiplier = 1))

    replacementEffect(
        EntersWithKeywords(
            keywords = listOf(Keyword.FLYING, Keyword.TRAMPLE),
            condition = Conditions.SourceCounterCountAtLeast(CounterTypeFilter.PlusOnePlusOne, 1),
            selfOnly = true,
        ),
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "111"
        artist = "Daarken"
        flavorText = "A killing machine from birth."
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d09a3b69-1214-420d-984f-0b2a043a9dc2.jpg?1783942468"
    }
}
