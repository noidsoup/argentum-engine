package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Earth-Origin Yak — Global Series: Jiang Yanggu & Mu Yanling #9
 * {3}{W} · Creature — Ox · 2/4
 *
 * When this creature enters, creatures you control get +1/+1 until end of turn.
 */
val EarthOriginYak = card("Earth-Origin Yak") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Ox"
    power = 2
    toughness = 4
    oracleText = "When this creature enters, creatures you control get +1/+1 until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.ModifyStats(1, 1, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "9"
        artist = "Tan Yan Yao"
        flavorText = "Deep in the Northern Mountains, the yaks linger silently, channeling the powers of the earth."
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abcde784-d0cd-4bac-b1bf-bd686ac2f73d.jpg?1783934634"
    }
}
