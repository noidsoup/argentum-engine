package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vampiric Dragon
 * {6}{B}{R}
 * Creature — Vampire Dragon
 * 5/5
 *
 * Flying
 * Whenever a creature dealt damage by this creature this turn dies, put a +1/+1 counter on this
 * creature.
 * {1}{R}: This creature deals 1 damage to target creature.
 *
 * Canonical printing: Odyssey (ODY). The death trigger is [Triggers.CreatureDealtDamageByThisDies]
 * (Predator Ooze / Zurgo Helmsmasher shape). The activated ability is a single-target
 * [Effects.DealDamage].
 */
val VampiricDragon = card("Vampiric Dragon") {
    manaCost = "{6}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Vampire Dragon"
    oracleText = "Flying\n" +
        "Whenever a creature dealt damage by this creature this turn dies, put a +1/+1 counter " +
        "on this creature.\n" +
        "{1}{R}: This creature deals 1 damage to target creature."
    power = 5
    toughness = 5

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.CreatureDealtDamageByThisDies
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever a creature dealt damage by this creature this turn dies, put a " +
            "+1/+1 counter on this creature."
    }

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(1, creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "296"
        artist = "Gary Ruddell"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f21d595-c248-4aae-9fd7-4e5787ab8781.jpg?1783945205"
    }
}
