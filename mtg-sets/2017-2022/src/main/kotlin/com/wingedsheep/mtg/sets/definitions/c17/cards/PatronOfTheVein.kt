package com.wingedsheep.mtg.sets.definitions.c17.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Patron of the Vein
 * {4}{B}{B}
 * Creature — Vampire Shaman
 * 4/4
 *
 * Flying
 * When this creature enters, destroy target creature an opponent controls.
 * Whenever a creature an opponent controls dies, exile it and put a +1/+1 counter on each
 * Vampire you control.
 */
val PatronOfTheVein = card("Patron of the Vein") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Shaman"
    power = 4
    toughness = 4
    oracleText =
        "Flying\n" +
            "When this creature enters, destroy target creature an opponent controls.\n" +
            "Whenever a creature an opponent controls dies, exile it and put a +1/+1 counter on " +
            "each Vampire you control."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target("target", Targets.CreatureOpponentControls)
        effect = Effects.Destroy(victim)
        description = "When this creature enters, destroy target creature an opponent controls."
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.opponentControls(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Exile(EffectTarget.TriggeringEntity, fromZone = Zone.GRAVEYARD).then(
            Effects.ForEachInGroup(
                filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE).youControl()),
                effect = AddCountersEffect(
                    counterType = Counters.PLUS_ONE_PLUS_ONE,
                    count = 1,
                    target = EffectTarget.Self,
                ),
            ),
        )
        description =
            "Whenever a creature an opponent controls dies, exile it and put a +1/+1 counter on " +
            "each Vampire you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "20"
        artist = "Tommy Arnold"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e27db39c-c4b8-46b9-afe6-1737841bdad6.jpg?1783935944"
    }
}
