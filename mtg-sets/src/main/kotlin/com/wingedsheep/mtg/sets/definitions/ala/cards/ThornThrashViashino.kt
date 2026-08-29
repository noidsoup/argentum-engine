package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EntersWithDevour
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Thorn-Thrash Viashino
 * {3}{R}
 * Creature — Lizard Warrior
 * 2/2
 *
 * Devour 2 (As this creature enters, you may sacrifice any number of creatures. It enters with
 * twice that many +1/+1 counters on it.)
 * {G}: This creature gains trample until end of turn.
 */
val ThornThrashViashino = card("Thorn-Thrash Viashino") {
    manaCost = "{3}{R}"
    colorIdentity = "RG"
    typeLine = "Creature — Lizard Warrior"
    oracleText = "Devour 2 (As this creature enters, you may sacrifice any number of creatures. " +
        "It enters with twice that many +1/+1 counters on it.)\n" +
        "{G}: This creature gains trample until end of turn."
    power = 2
    toughness = 2

    keywords(Keyword.DEVOUR)
    keywordAbility(KeywordAbility.devour(2))
    replacementEffect(EntersWithDevour(multiplier = 2))

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "116"
        artist = "Jon Foster"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7987de9-d812-4615-845a-3a90572d9b44.jpg?1783942557"
    }
}
