package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Qilin's Blessing — Global Series: Jiang Yanggu & Mu Yanling #14
 * {W} · Instant
 *
 * Target creature gets +2/+2 until end of turn.
 */
val QilinsBlessing = card("Qilin's Blessing") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 until end of turn."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Wolk Sheep"
        flavorText =
            "\"Interesting . . . This mortal being seems to have been blessed with Qilin's essence.\"\n" +
                "—Mu Yanling's journal"
        imageUri = "https://cards.scryfall.io/normal/front/0/2/028ad74f-8366-4c14-b532-63fa892f5784.jpg?1783934633"
    }
}
