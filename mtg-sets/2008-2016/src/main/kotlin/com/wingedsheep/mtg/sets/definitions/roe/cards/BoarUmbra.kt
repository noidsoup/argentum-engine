package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.umbraArmor
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Boar Umbra
 * {2}{G}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +3/+3.
 * Umbra armor
 */
val BoarUmbra = card("Boar Umbra") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +3/+3.\n" +
        "Umbra armor (If enchanted creature would be destroyed, instead remove all damage from it and destroy this Aura.)"

    auraTarget = Targets.Creature
    umbraArmor()

    staticAbility {
        ability = ModifyStats(+3, +3, Filters.EnchantedCreature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "179"
        artist = "Christopher Moeller"
        flavorText = "\"From in here, everything looks like dinner.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8dfdec4f-e66a-48f8-ba6d-13459e80b52c.jpg"
    }
}
