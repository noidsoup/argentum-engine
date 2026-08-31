package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Confidence from Strength — Global Series: Jiang Yanggu & Mu Yanling #35
 * {2}{G} · Sorcery
 *
 * Target creature gets +4/+4 and gains trample until end of turn.
 */
val ConfidenceFromStrength = card("Confidence from Strength") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText =
        "Target creature gets +4/+4 and gains trample until end of turn. " +
            "(It can deal excess combat damage to the player or planeswalker it's attacking.)"

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(4, 4, t)
            .then(Effects.GrantKeyword(Keyword.TRAMPLE, t))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "Shinchuen Chen"
        flavorText = "\"Prepare for battle!\"\n—Jiang Yanggu"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98945e26-61e5-457d-ba8c-def924a7b1d1.jpg?1783934625"
    }
}
