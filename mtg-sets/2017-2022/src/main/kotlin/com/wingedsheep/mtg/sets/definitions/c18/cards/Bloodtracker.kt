package com.wingedsheep.mtg.sets.definitions.c18.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Bloodtracker
 * {3}{B}
 * Creature — Vampire Wizard
 * 2/2
 *
 * Flying
 * {B}, Pay 2 life: Put a +1/+1 counter on this creature.
 * When this creature leaves the battlefield, draw a card for each +1/+1 counter on it.
 *
 * The leaves trigger reads [ContextPropertyKey.LAST_KNOWN_PLUS_ONE_COUNTER_COUNT] (Marketback
 * Walker / Arcbound Condor idiom): by resolution the Vampire is off the battlefield and its
 * counters are gone, so the draw count must come from last-known information.
 */
val Bloodtracker = card("Bloodtracker") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Wizard"
    oracleText = "Flying\n" +
        "{B}, Pay 2 life: Put a +1/+1 counter on this creature.\n" +
        "When this creature leaves the battlefield, draw a card for each +1/+1 counter on it."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{B}"),
            Costs.PayLife(2),
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "{B}, Pay 2 life: Put a +1/+1 counter on this creature."
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.DrawCards(
            DynamicAmount.ContextProperty(ContextPropertyKey.LAST_KNOWN_PLUS_ONE_COUNTER_COUNT),
        )
        description = "When this creature leaves the battlefield, draw a card for each +1/+1 " +
            "counter on it."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "14"
        artist = "Magali Villeneuve"
        flavorText = "Flee all you like. The further you run the more firmly I feel your heartbeat."
        imageUri = "https://cards.scryfall.io/normal/front/c/a/ca3519c4-4d8c-4caf-bc93-6e6160a3d5b6.jpg?1783934340"
    }
}
