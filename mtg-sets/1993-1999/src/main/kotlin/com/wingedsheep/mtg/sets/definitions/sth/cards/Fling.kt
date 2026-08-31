package com.wingedsheep.mtg.sets.definitions.sth.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Fling
 * {1}{R}
 * Instant
 *
 * As an additional cost to cast this spell, sacrifice a creature.
 * Fling deals damage equal to the sacrificed creature's power to any target.
 */
val Fling = card("Fling") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature.\n" +
        "Fling deals damage equal to the sacrificed creature's power to any target."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Creature))

    spell {
        val t = target("any target", AnyTarget())
        effect = DealDamageEffect(DynamicAmounts.sacrificedPower(), t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Paolo Parente"
        flavorText = "It's raining rats and moggs."
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b144452-2e91-4e46-abe9-ed76b39f8314.jpg?1783946556"
    }
}
