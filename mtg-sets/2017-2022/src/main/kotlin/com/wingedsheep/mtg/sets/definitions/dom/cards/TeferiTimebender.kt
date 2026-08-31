package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.TakeExtraTurnEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Teferi, Timebender
 * {4}{W}{U}
 * Legendary Planeswalker — Teferi
 * Starting loyalty: 5
 *
 * +2: Untap up to one target artifact or creature.
 * −3: You gain 2 life and draw two cards.
 * −9: Take an extra turn after this one.
 */
val TeferiTimebender = card("Teferi, Timebender") {
    manaCost = "{4}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Planeswalker — Teferi"
    startingLoyalty = 5
    oracleText = "+2: Untap up to one target artifact or creature.\n" +
        "\u22123: You gain 2 life and draw two cards.\n" +
        "\u22129: Take an extra turn after this one."

    loyaltyAbility(+2) {
        val t = target(
            "up to one target artifact or creature",
            TargetPermanent(optional = true, filter = TargetFilter.CreatureOrArtifact),
        )
        effect = Effects.Untap(t)
    }

    loyaltyAbility(-3) {
        effect = Effects.Composite(
            Effects.GainLife(2),
            Effects.DrawCards(2),
        )
    }

    loyaltyAbility(-9) {
        effect = TakeExtraTurnEffect()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "270"
        artist = "Zack Stella"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb2b7388-ac6b-45c0-a5cc-da6450724b59.jpg?1783934938"
    }
}
