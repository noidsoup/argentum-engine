package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDevour
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Gluttonous Slime
 * {2}{G}
 * Creature — Ooze
 * 2/2
 *
 * Flash
 * Devour 1 (As this creature enters, you may sacrifice any number of creatures. It enters with
 * that many +1/+1 counters on it.)
 */
val GluttonousSlime = card("Gluttonous Slime") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ooze"
    oracleText = "Flash\n" +
        "Devour 1 (As this creature enters, you may sacrifice any number of creatures. " +
        "It enters with that many +1/+1 counters on it.)"
    power = 2
    toughness = 2

    keywords(Keyword.FLASH, Keyword.DEVOUR)
    keywordAbility(KeywordAbility.devour(1))

    replacementEffect(EntersWithDevour(multiplier = 1))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "83"
        artist = "Trevor Claxton"
        flavorText = "On Jund, everything eventually ends up in something else's stomach."
        imageUri = "https://cards.scryfall.io/normal/front/2/5/258c7201-02b0-4e16-9fa6-0a79631e7724.jpg?1783942475"
    }
}
