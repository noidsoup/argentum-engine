package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Jiang Yanggu — Global Series: Jiang Yanggu & Mu Yanling #22
 * {4}{G} · Legendary Planeswalker — Yanggu
 *
 * +1: Target creature gets +2/+2 until end of turn.
 * −1: If you don't control a creature named Mowu, create Mowu, a legendary 3/3 green Dog creature token.
 * −5: Until end of turn, target creature gains trample and gets +X/+X, where X is the number of lands you control.
 */
val JiangYanggu = card("Jiang Yanggu") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Planeswalker — Yanggu"
    startingLoyalty = 4
    oracleText =
        "+1: Target creature gets +2/+2 until end of turn.\n" +
            "\u22121: If you don't control a creature named Mowu, create Mowu, a legendary 3/3 green Dog creature token.\n" +
            "\u22125: Until end of turn, target creature gains trample and gets +X/+X, where X is the number of lands you control."

    loyaltyAbility(+1) {
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, t)
    }

    loyaltyAbility(-1) {
        effect = ConditionalEffect(
            condition = Conditions.YouControl(
                GameObjectFilter.Creature.named("Mowu"),
                negate = true,
            ),
            effect = CreateTokenEffect(
                count = com.wingedsheep.sdk.scripting.values.DynamicAmount.Fixed(1),
                power = 3,
                toughness = 3,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Dog"),
                name = "Mowu",
                legendary = true,
                imageUri = "https://cards.scryfall.io/normal/front/b/1/b10441dd-9029-4f95-9566-d3771ebd36bd.jpg?1783934643",
            ),
        )
    }

    loyaltyAbility(-5) {
        val t = target("target creature", Targets.Creature)
        val lands = DynamicAmounts.landsYouControl()
        effect = Effects.GrantKeyword(Keyword.TRAMPLE, t)
            .then(Effects.ModifyStats(lands, lands, t))
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "22"
        artist = "Tingting Yeh"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb8dae9a-dac2-4b80-b546-852b989bd8c1.jpg?1783934627"
    }
}
