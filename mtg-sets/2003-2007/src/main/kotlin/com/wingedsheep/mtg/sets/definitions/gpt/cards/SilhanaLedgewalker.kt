package com.wingedsheep.mtg.sets.definitions.gpt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedExceptBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Silhana Ledgewalker
 * {1}{G}
 * Creature — Elf Rogue
 * 1/1
 *
 * Hexproof
 * This creature can't be blocked except by creatures with flying.
 */
val SilhanaLedgewalker = card("Silhana Ledgewalker") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Rogue"
    oracleText = "Hexproof (This creature can't be the target of spells or abilities your opponents control.)\n" +
        "This creature can't be blocked except by creatures with flying."
    power = 1
    toughness = 1

    keywords(Keyword.HEXPROOF)

    staticAbility {
        ability = CantBeBlockedExceptBy(blockerFilter = GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "James Wong"
        flavorText = "Street folk call them \"spire mice,\" but behind the mockery is an unspoken envy of the ledgewalkers' skill at avoiding harm."
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c921343a-4496-4e21-a0ff-e04a1fb407bf.jpg?1783943490"
    }
}
