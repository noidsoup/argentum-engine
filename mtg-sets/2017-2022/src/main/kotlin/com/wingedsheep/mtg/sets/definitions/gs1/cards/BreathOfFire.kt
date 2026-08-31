package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Breath of Fire — Global Series: Jiang Yanggu & Mu Yanling #33
 * {1}{R} · Instant
 *
 * Breath of Fire deals 2 damage to target creature.
 */
val BreathOfFire = card("Breath of Fire") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Breath of Fire deals 2 damage to target creature."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "33"
        artist = "Xin-Yu Liu"
        flavorText = "Fire leads to more fire."
        imageUri = "https://cards.scryfall.io/normal/front/4/1/41f2874a-2417-41cb-9cec-551eea78473c.jpg?1783934625"
    }
}
