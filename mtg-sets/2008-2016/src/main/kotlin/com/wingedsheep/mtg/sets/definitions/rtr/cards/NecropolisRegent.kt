package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Necropolis Regent
 * {3}{B}{B}{B}
 * Creature — Vampire
 * 6/5
 *
 * Flying
 * Whenever a creature you control deals combat damage to a player, put that many +1/+1 counters on it.
 *
 * The counters trigger is battlefield-wide ([TriggerBinding.ANY] over
 * [GameObjectFilter.Creature.youControl]), and "that many" reads the damage off the trigger
 * payload ([ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT]) while "on it" is the damage source
 * ([EffectTarget.TriggeringEntity]).
 */
val NecropolisRegent = card("Necropolis Regent") {
    manaCost = "{3}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 6
    toughness = 5
    oracleText = "Flying\n" +
        "Whenever a creature you control deals combat damage to a player, put that many +1/+1 " +
        "counters on it."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            sourceFilter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.AddDynamicCounters(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            amount = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
            target = EffectTarget.TriggeringEntity,
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "71"
        artist = "Winona Nelson"
        flavorText = "\"Jarad fancies himself king of the undercity, but he's merely king of rot.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b421dcc9-0299-416d-86bc-c70ef49bcf98.jpg?1783940361"
    }
}
