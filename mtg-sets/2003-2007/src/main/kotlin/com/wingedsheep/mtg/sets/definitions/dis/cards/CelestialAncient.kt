package com.wingedsheep.mtg.sets.definitions.dis.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Celestial Ancient
 * {3}{W}{W}
 * Creature — Elemental
 * 3/3
 *
 * Flying
 * Whenever you cast an enchantment spell, put a +1/+1 counter on each creature you control.
 */
val CelestialAncient = card("Celestial Ancient") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "Whenever you cast an enchantment spell, put a +1/+1 counter on each creature you control."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastEnchantment
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Creature.youControl()),
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "7"
        artist = "Mark Tedin"
        flavorText = "\"We thought the clouds had moved from the night sky. Then the night sky moved, " +
            "and the horizon grew wings.\"\n—Josuri"
        imageUri = "https://cards.scryfall.io/normal/front/0/1/012fb3a6-99ff-4b5e-96e3-0a2eeca36bdb.jpg?1783942533"
    }
}
