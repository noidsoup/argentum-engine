package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Karplusan Hound
 * {3}{R}
 * Creature — Dog
 * 3/3
 * Whenever this creature attacks, if you control a Chandra planeswalker, this creature deals 2
 * damage to any target.
 */
val KarplusanHound = card("Karplusan Hound") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dog"
    oracleText =
        "Whenever this creature attacks, if you control a Chandra planeswalker, this creature deals 2 damage to any target."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.Attacks
        // Intervening-if (CR 603.4): "…attacks, if you control a Chandra planeswalker…"
        triggerCondition = Conditions.YouControl(
            GameObjectFilter.Planeswalker.withSubtype("Chandra"),
        )
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, t)
        description =
            "Whenever this creature attacks, if you control a Chandra planeswalker, this creature deals 2 damage to any target."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "277"
        artist = "Viktor Titov"
        flavorText = "\"Don't worry, they don't bite. They much prefer setting people on fire.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/5/95179b31-8cb5-4dd9-ad93-782b8774534d.jpg?1783934935"
    }
}
