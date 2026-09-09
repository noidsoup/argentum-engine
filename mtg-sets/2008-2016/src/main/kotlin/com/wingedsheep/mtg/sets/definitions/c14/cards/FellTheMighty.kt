package com.wingedsheep.mtg.sets.definitions.c14.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Fell the Mighty
 * {4}{W}
 * Sorcery
 *
 * Destroy all creatures with power greater than target creature's power.
 *
 * The comparison reads the targeted creature's projected power at resolution (CR 608.2b), so
 * temporary boosts and shrink effects on the target are respected when the destroy-all sweep runs.
 */
val FellTheMighty = card("Fell the Mighty") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy all creatures with power greater than target creature's power."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DestroyAll(
            GameObjectFilter.Creature.powerGreaterThanEntity(EntityReference.Target(0)),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "7"
        artist = "Raymond Swanland"
        flavorText = "\"They had it coming.\"\n—Squire Imalia, savior of Thiswick"
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d4e999d3-c2d7-47dc-81ad-a2baf6cc4757.jpg?1783938873"
    }
}
