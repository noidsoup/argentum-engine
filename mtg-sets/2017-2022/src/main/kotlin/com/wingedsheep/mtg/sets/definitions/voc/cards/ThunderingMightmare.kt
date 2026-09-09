package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.soulbond
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Thundering Mightmare
 * {4}{G}
 * Creature — Horse Spirit
 * 3/3
 *
 * Soulbond (You may pair this creature with another unpaired creature when either enters. They
 * remain paired for as long as you control both of them.)
 * As long as Thundering Mightmare is paired with another creature, each of those creatures has
 * "Whenever an opponent casts a spell, put a +1/+1 counter on this creature."
 *
 * [GrantTriggeredAbility] over [GroupFilter.soulbondPair] — the same shape as [TandemLookout] and
 * [MiragePhalanx]. [TriggerBinding.SELF] makes "this creature" mean the half that heard the cast,
 * so each paired creature grows independently when an opponent casts.
 */
val ThunderingMightmare = card("Thundering Mightmare") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Horse Spirit"
    oracleText =
        "Soulbond (You may pair this creature with another unpaired creature when either enters. " +
            "They remain paired for as long as you control both of them.)\n" +
            "As long as Thundering Mightmare is paired with another creature, each of those creatures has " +
            "\"Whenever an opponent casts a spell, put a +1/+1 counter on this creature.\""
    power = 3
    toughness = 3

    soulbond()

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.OpponentCastsSpell.event,
                binding = TriggerBinding.SELF,
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                descriptionOverride =
                    "Whenever an opponent casts a spell, put a +1/+1 counter on this creature.",
            ),
            filter = GroupFilter.soulbondPair(),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "37"
        artist = "Lorenzo Mastroianni"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a37fbffb-4ed0-4c61-9cef-2cd55ad67f9c.jpg?1783924991"
    }
}
