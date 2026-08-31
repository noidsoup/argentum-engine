package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.umbraArmor
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Mammoth Umbra
 * {4}{W}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +3/+3 and has vigilance.
 * Umbra armor
 */
val MammothUmbra = card("Mammoth Umbra") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +3/+3 and has vigilance.\n" +
        "Umbra armor (If enchanted creature would be destroyed, instead remove all damage from it and destroy this Aura.)"

    auraTarget = Targets.Creature
    umbraArmor()

    staticAbility {
        ability = ModifyStats(+3, +3, Filters.EnchantedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE, Filters.EnchantedCreature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "37"
        artist = "Christopher Moeller"
        flavorText = "\"The mammoth's hide is a canvas for the shamans, its soul a dwelling for ancestral spirits.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76f1abef-385d-4202-8a45-835c37c4e242.jpg"
    }
}
