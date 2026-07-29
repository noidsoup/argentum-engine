package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cleansing Screech — Global Series: Jiang Yanggu & Mu Yanling #37
 * {4}{R} · Sorcery
 *
 * Cleansing Screech deals 4 damage to any target.
 */
val CleansingScreech = card("Cleansing Screech") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Cleansing Screech deals 4 damage to any target."

    spell {
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(4, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Tingting Yeh"
        flavorText = "\"Crackle\" and \"pop\" are the most wondrous sounds."
        imageUri = "https://cards.scryfall.io/normal/front/7/9/79928b26-fcac-4c3f-9edd-292769c2e56e.jpg?1783934622"
    }
}
