package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Bramble Elemental
 * {3}{G}{G}
 * Creature — Elemental
 * 4/4
 * Whenever an Aura becomes attached to this creature, create two 1/1 green Saproling creature tokens.
 */
val BrambleElemental = card("Bramble Elemental") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    oracleText =
        "Whenever an Aura becomes attached to this creature, create two 1/1 green Saproling creature tokens."
    power = 4
    toughness = 4
    triggeredAbility {
        trigger = Triggers.becomesAttached(
            attachmentFilter = GameObjectFilter.Enchantment.withSubtype("Aura"),
            attachedToFilter = GameObjectFilter.Permanent.sourceItself(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling"),
            count = 2,
        )
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "154"
        artist = "Tomas Giorello"
        flavorText =
            "Ravnica is a seamless urban tapestry, each city bleeding into the next. In abandoned " +
                "corners, however, nature has begun to reclaim what it once owned."
        imageUri = "https://cards.scryfall.io/normal/front/4/7/470665e5-fa48-4772-ba71-5d4008d042f3.jpg"
    }
}
