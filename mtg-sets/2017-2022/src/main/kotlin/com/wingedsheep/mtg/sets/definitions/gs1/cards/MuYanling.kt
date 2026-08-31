package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.TakeExtraTurnEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mu Yanling — Global Series: Jiang Yanggu & Mu Yanling #1
 * {4}{U}{U} · Legendary Planeswalker — Yanling
 *
 * +2: Target creature can't be blocked this turn.
 * −3: Draw two cards.
 * −10: Tap all creatures your opponents control. You take an extra turn after this one.
 */
val MuYanling = card("Mu Yanling") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Planeswalker — Yanling"
    startingLoyalty = 5
    oracleText =
        "+2: Target creature can't be blocked this turn.\n" +
            "\u22123: Draw two cards.\n" +
            "\u221210: Tap all creatures your opponents control. You take an extra turn after this one."

    loyaltyAbility(+2) {
        val t = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, t)
    }

    loyaltyAbility(-3) {
        effect = Effects.DrawCards(2)
    }

    loyaltyAbility(-10) {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.opponentControls()),
            Effects.Tap(EffectTarget.Self),
        ).then(TakeExtraTurnEffect())
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "1"
        artist = "林玄泰"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2ee3c3c-768b-434b-a121-6d897b2ae345.jpg?1783934637"
    }
}
