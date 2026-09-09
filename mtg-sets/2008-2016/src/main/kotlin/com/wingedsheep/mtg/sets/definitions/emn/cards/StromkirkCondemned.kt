package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stromkirk Condemned (Eldritch Moon #106)
 * {B}{B}
 * Creature — Vampire Horror
 * 2/2
 *
 * Discard a card: Vampires you control get +1/+1 until end of turn. Activate only once each turn.
 *
 * The discard is the entire activation cost ([Costs.DiscardCard]), which enables madness on the
 * discarded card. "Activate only once each turn" is [ActivationRestriction.OncePerTurn]. The pump
 * is a group iteration over Vampire creatures you control, including this one.
 */
val StromkirkCondemned = card("Stromkirk Condemned") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Horror"
    power = 2
    toughness = 2
    oracleText = "Discard a card: Vampires you control get +1/+1 until end of turn. " +
        "Activate only once each turn."

    activatedAbility {
        cost = Costs.DiscardCard
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE).youControl()),
            Effects.ModifyStats(1, 1, EffectTarget.Self)
        )
        restrictions = listOf(ActivationRestriction.OncePerTurn)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "106"
        artist = "Magali Villeneuve"
        flavorText = "\"Blood from the vein is the finest vintage to accompany a feast of the mind.\"\n—Runo Stromkirk"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4a8403e-bf4c-4aae-9102-188f49c61ddf.jpg?1783937473"
    }
}
