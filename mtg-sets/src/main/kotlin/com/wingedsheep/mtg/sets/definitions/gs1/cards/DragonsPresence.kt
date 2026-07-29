package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Dragon's Presence — Global Series: Jiang Yanggu & Mu Yanling #16
 * {2}{W} · Instant
 *
 * Dragon's Presence deals 5 damage to target attacking or blocking creature.
 */
val DragonsPresence = card("Dragon's Presence") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Dragon's Presence deals 5 damage to target attacking or blocking creature."

    spell {
        val t = target(
            "target attacking or blocking creature",
            TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature),
        )
        effect = Effects.DealDamage(5, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "16"
        artist = "Tan Yan Yao"
        flavorText =
            "\"The Ancestor Dragon appeared atop the clouds, amid shattering mountains and sundering earth.\"\n—Jiang Yanggu's travelogue"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53855022-7aff-4d13-bdd8-0abbf69204a4.jpg?1783934631"
    }
}
