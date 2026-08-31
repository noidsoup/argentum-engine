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
 * Hyena Umbra
 * {W}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +1/+1 and has first strike.
 * Umbra armor
 */
val HyenaUmbra = card("Hyena Umbra") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+1 and has first strike.\n" +
        "Umbra armor (If enchanted creature would be destroyed, instead remove all damage from it and destroy this Aura.)"

    auraTarget = Targets.Creature
    umbraArmor()

    staticAbility {
        ability = ModifyStats(+1, +1, Filters.EnchantedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE, Filters.EnchantedCreature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Christopher Moeller"
        flavorText = "\"I am bound to all my children. We share one hide, one heart, one hunger.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f1461e2f-fb1d-4092-9935-4cee092f27e7.jpg"
    }
}
