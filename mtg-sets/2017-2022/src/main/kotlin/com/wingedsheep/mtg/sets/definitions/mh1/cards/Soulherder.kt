package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Soulherder
 * {1}{W}{U}
 * Creature — Spirit
 * 1/1
 *
 * Whenever a creature is exiled from the battlefield, put a +1/+1 counter on this creature.
 * At the beginning of your end step, you may exile another target creature you control, then
 * return that card to the battlefield under its owner's control.
 */
val Soulherder = card("Soulherder") {
    manaCost = "{1}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 1
    oracleText = "Whenever a creature is exiled from the battlefield, put a +1/+1 counter on " +
        "this creature.\nAt the beginning of your end step, you may exile another target creature " +
        "you control, then return that card to the battlefield under its owner's control."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature,
            to = Zone.EXILE,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever a creature is exiled from the battlefield, put a +1/+1 counter on this creature."
    }

    triggeredAbility {
        trigger = Triggers.YourEndStep
        val creature = target("another target creature you control", Targets.OtherCreatureYouControl)
        effect = MayEffect(
            Effects.Move(creature, Zone.EXILE)
                .then(Effects.Move(creature, Zone.BATTLEFIELD)),
        )
        description = "At the beginning of your end step, you may exile another target creature " +
            "you control, then return that card to the battlefield under its owner's control."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "214"
        artist = "Seb McKinnon"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea23111b-ccc1-4d5c-a9d2-9db14c728820.jpg?1783933079"
    }
}
