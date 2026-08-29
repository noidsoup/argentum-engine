package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantProtectionFromCardType
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Spirit Mantle
 * {1}{W}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +1/+1 and has protection from creatures.
 */
val SpiritMantle = card("Spirit Mantle") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+1 and has protection from creatures."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    staticAbility {
        ability = GrantProtectionFromCardType(CardType.CREATURE)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "35"
        artist = "Izzy"
        flavorText = "The shield of unimpeachable purity is as strong as any wrought on the anvil."
        imageUri = "https://cards.scryfall.io/normal/front/9/3/930c8444-ccce-411e-bc4f-e5abca749608.jpg?1783941097"
    }
}
