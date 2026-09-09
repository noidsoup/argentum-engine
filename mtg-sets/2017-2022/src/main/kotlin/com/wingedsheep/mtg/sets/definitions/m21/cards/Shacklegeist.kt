package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CanOnlyBlockCreaturesWith
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Shacklegeist
 * {1}{U}
 * Creature — Spirit
 * 2/2
 *
 * Flying
 * This creature can block only creatures with flying.
 * Tap two untapped Spirits you control: Tap target creature you don't control.
 */
val Shacklegeist = card("Shacklegeist") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    oracleText = "Flying\n" +
        "This creature can block only creatures with flying.\n" +
        "Tap two untapped Spirits you control: Tap target creature you don't control."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CanOnlyBlockCreaturesWith(
            blockerFilter = GameObjectFilter.Creature.withKeyword(Keyword.FLYING),
        )
    }

    activatedAbility {
        cost = Costs.TapPermanents(
            count = 2,
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.SPIRIT),
        )
        val creature = target(
            "target creature you don't control",
            TargetCreature(filter = TargetFilter.Creature.opponentControls()),
        )
        effect = Effects.Tap(creature)
        description = "Tap target creature you don't control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "70"
        artist = "Igor Kieryluk"
        flavorText = "The Blessed Sleep doesn't come easily to a mind chained by regrets."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a5a88e3-e73c-4b34-a645-06fe27e68cee.jpg?1783930719"
    }
}
