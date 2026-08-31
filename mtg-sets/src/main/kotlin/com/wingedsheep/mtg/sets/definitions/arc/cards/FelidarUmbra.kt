package com.wingedsheep.mtg.sets.definitions.arc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.umbraArmor
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Felidar Umbra
 * {1}{W}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has lifelink.
 * {1}{W}: Attach this Aura to target creature.
 * Umbra armor
 */
val FelidarUmbra = card("Felidar Umbra") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has lifelink.\n" +
        "{1}{W}: Attach this Aura to target creature.\n" +
        "Umbra armor (If enchanted creature would be destroyed, instead remove all damage from it and destroy this Aura.)"

    auraTarget = Targets.Creature
    umbraArmor()

    staticAbility {
        ability = GrantKeyword(Keyword.LIFELINK, Filters.EnchantedCreature)
    }

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.AttachEquipment(t)
        description = "{1}{W}: Attach this Aura to target creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "6"
        artist = "Christopher Moeller"
        flavorText = "\"Only the most disciplined mage can truly ride the felidar.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fe83a23c-723e-4b34-a121-a10fc0efe1b5.jpg"
    }
}
