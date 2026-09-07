package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dovin, Grand Arbiter
 * {1}{W}{U}
 * Legendary Planeswalker — Dovin
 * Loyalty 3
 *
 * +1: Until end of turn, whenever a creature you control deals combat damage to a player, put a
 *     loyalty counter on Dovin.
 * −1: Create a 1/1 colorless Thopter artifact creature token with flying. You gain 1 life.
 * −7: Look at the top ten cards of your library. Put three of them into your hand and the rest on
 *     the bottom of your library in a random order.
 *
 * The +1 is the Mistway Spy floating-trigger shape: a global until-end-of-turn watcher for combat
 * damage from any creature you control, putting loyalty on Dovin rather than investigating.
 */
val DovinGrandArbiter = card("Dovin, Grand Arbiter") {
    manaCost = "{1}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Planeswalker — Dovin"
    startingLoyalty = 3
    oracleText = "+1: Until end of turn, whenever a creature you control deals combat damage to a " +
        "player, put a loyalty counter on Dovin.\n" +
        "−1: Create a 1/1 colorless Thopter artifact creature token with flying. You gain 1 life.\n" +
        "−7: Look at the top ten cards of your library. Put three of them into your hand and the " +
        "rest on the bottom of your library in a random order."

    loyaltyAbility(+1) {
        effect = Effects.CreateGlobalTriggeredAbility(
            duration = Duration.EndOfTurn,
            ability = TriggeredAbility.create(
                trigger = Triggers.dealsDamage(
                    damageType = DamageType.Combat,
                    recipient = RecipientFilter.AnyPlayer,
                    sourceFilter = GameObjectFilter.Creature.youControl(),
                    binding = TriggerBinding.ANY,
                ).event,
                binding = TriggerBinding.ANY,
                effect = Effects.AddCounters(Counters.LOYALTY, 1, EffectTarget.Self),
                descriptionOverride = "Whenever a creature you control deals combat damage to a " +
                    "player, put a loyalty counter on Dovin.",
            ),
            descriptionOverride = "Until end of turn, whenever a creature you control deals " +
                "combat damage to a player, put a loyalty counter on Dovin.",
        )
    }

    loyaltyAbility(-1) {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            creatureTypes = setOf("Thopter"),
            keywords = setOf(Keyword.FLYING),
            artifactToken = true,
        ) then Effects.GainLife(1)
    }

    loyaltyAbility(-7) {
        effect = Patterns.Library.lookAtTopAndKeep(
            count = 10,
            keepCount = 3,
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
            restOrder = CardOrder.Random,
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "167"
        artist = "Kieran Yanner"
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e6784910-0204-4a39-bb38-50daa03e94c2.jpg?1783933653"
    }
}
