package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Reckless Pangolin — Global Series: Jiang Yanggu & Mu Yanling #26
 * {2}{G} · Creature — Pangolin · 2/2
 *
 * Whenever this creature attacks, it gets +1/+1 until end of turn.
 */
val RecklessPangolin = card("Reckless Pangolin") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Pangolin"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature attacks, it gets +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "26"
        artist = "Wolk Sheep"
        flavorText =
            "\"A pangolin's power grows with its speed. Strike when you have the chance!\"\n" +
                "—Jiang Yanggu's travelogue"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/4662c681-fcc9-4fc8-a598-b068208132fd.jpg?1783934626"
    }
}
