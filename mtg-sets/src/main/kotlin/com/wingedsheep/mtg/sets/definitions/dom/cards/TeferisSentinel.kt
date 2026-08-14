package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Teferi's Sentinel
 * {5}
 * Artifact Creature — Golem
 * 2/6
 * As long as you control a Teferi planeswalker, this creature gets +4/+0.
 */
val TeferisSentinel = card("Teferi's Sentinel") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    oracleText = "As long as you control a Teferi planeswalker, this creature gets +4/+0."
    power = 2
    toughness = 6

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(
                powerBonus = 4,
                toughnessBonus = 0,
                filter = GroupFilter.source(),
            ),
            condition = Conditions.YouControl(
                GameObjectFilter.Planeswalker.withSubtype("Teferi"),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "273"
        artist = "Titus Lunter"
        flavorText = "\"Long ago, I enchanted an army of statues to guard Zhalfir. My homeland may be gone, but its protectors remain.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/5/058304f4-f08a-4b3a-ae74-c06f4968c99a.jpg?1783934937"
    }
}
