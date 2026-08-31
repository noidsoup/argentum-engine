package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Hardened-Scale Armor — Global Series: Jiang Yanggu & Mu Yanling #32
 * {2}{G} · Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +3/+3.
 */
val HardenedScaleArmor = card("Hardened-Scale Armor") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature gets +3/+3."

    auraTarget = Targets.Creature
    staticAbility {
        ability = ModifyStats(3, 3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Tan Yan Yao"
        flavorText =
            "A pangolin's scales are resistant to all sorts of damage and weaponry. " +
                "Desire for them threatens to drive the pangolin to extinction."
        imageUri = "https://cards.scryfall.io/normal/front/7/0/708e7438-b449-490e-820f-9ecd3199541b.jpg?1783934625"
    }
}
