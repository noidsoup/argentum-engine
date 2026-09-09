package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cordial Vampire
 * {B}{B}
 * Creature — Vampire
 * 1/1
 *
 * Whenever this creature or another creature dies, put a +1/+1 counter on each Vampire you control.
 */
val CordialVampire = card("Cordial Vampire") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature or another creature dies, put a +1/+1 counter on each Vampire you control."

    triggeredAbility {
        trigger = Triggers.AnyCreatureDies
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE).youControl()),
            effect = AddCountersEffect(
                counterType = Counters.PLUS_ONE_PLUS_ONE,
                count = 1,
                target = EffectTarget.Self,
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "83"
        artist = "Winona Nelson"
        flavorText = "\"Please, come in! Hors d'oeuvres are on the table.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a90a1c44-ee0f-4c12-bfaf-6f371bcce167.jpg?1783933130"
    }
}
