package com.wingedsheep.mtg.sets.definitions.shm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.predicates.StatePredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Inkfathom Witch
 * {1}{U/B}
 * Creature — Merfolk Wizard
 * 1/1
 *
 * Fear (This creature can't be blocked except by artifact creatures and/or black creatures.)
 * {2}{U}{B}: Each unblocked creature has base power and toughness 4/1 until end of turn.
 *
 * "Unblocked" is [StatePredicate.IsUnblocked] (attackers with no blockers; non-attackers are
 * excluded). The [Effects.ForEachInGroup] / [EffectTarget.Self] pair applies the set-P/T to each
 * matched creature in turn (Layer 7b).
 */
val InkfathomWitch = card("Inkfathom Witch") {
    manaCost = "{1}{U/B}"
    colorIdentity = "UB"
    typeLine = "Creature — Merfolk Wizard"
    power = 1
    toughness = 1
    oracleText = "Fear (This creature can't be blocked except by artifact creatures and/or " +
        "black creatures.)\n" +
        "{2}{U}{B}: Each unblocked creature has base power and toughness 4/1 until end of turn."

    keywords(Keyword.FEAR)

    activatedAbility {
        cost = Costs.Mana("{2}{U}{B}")
        effect = Effects.ForEachInGroup(
            GroupFilter(
                GameObjectFilter.Creature.copy(
                    statePredicates = listOf(StatePredicate.IsUnblocked)
                )
            ),
            Effects.SetBasePowerAndToughness(4, 1, EffectTarget.Self, Duration.EndOfTurn)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "168"
        artist = "Larry MacDougall"
        flavorText = "The murk of the Wanderbrine concealed unseemly rituals designed to bring " +
            "out the worst in merrowkind."
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a48062c-faac-4183-830f-919ab255b907.jpg?1783942731"
    }
}
