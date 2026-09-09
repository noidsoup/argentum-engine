package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Olivia's Wrath
 * {4}{B}
 * Sorcery
 *
 * Each non-Vampire creature gets -X/-X until end of turn, where X is the number of Vampires you
 * control.
 *
 * Canonical printing: Innistrad: Crimson Vow Commander (VOC).
 */
val OliviasWrath = card("Olivia's Wrath") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Each non-Vampire creature gets -X/-X until end of turn, where X is the number of " +
        "Vampires you control."

    spell {
        val vampireCount = DynamicAmount.AggregateBattlefield(
            Player.You,
            GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE),
        )
        effect = Patterns.Group.modifyStatsForAll(
            power = DynamicAmount.Multiply(vampireCount, -1),
            toughness = DynamicAmount.Multiply(vampireCount, -1),
            filter = GroupFilter(GameObjectFilter.Creature.notSubtype(Subtype.VAMPIRE)),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "20"
        artist = "Néstor Ossandón Leal"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98893cc1-f502-4ca6-b6c1-e09fa1f4ef7a.jpg?1783925002"
    }
}
