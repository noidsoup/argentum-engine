package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDevour
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Thunder-Thrash Elder
 * {2}{R}
 * Creature — Lizard Warrior
 * 1/1
 *
 * Devour 3 (As this creature enters, you may sacrifice any number of creatures. It enters with
 * three times that many +1/+1 counters on it.)
 */
val ThunderThrashElder = card("Thunder-Thrash Elder") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard Warrior"
    oracleText = "Devour 3 (As this creature enters, you may sacrifice any number of creatures. " +
        "It enters with three times that many +1/+1 counters on it.)"
    power = 1
    toughness = 1

    keywords(Keyword.DEVOUR)
    keywordAbility(KeywordAbility.devour(3))
    replacementEffect(EntersWithDevour(multiplier = 3))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "117"
        artist = "Brandon Kitkouski"
        flavorText = "Viashino thrashes are led by elders who have survived countless challenges."
        imageUri = "https://cards.scryfall.io/normal/front/5/5/5552da28-01a3-4277-9e69-b297c3874ea4.jpg?1783942558"
    }
}
