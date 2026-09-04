package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Twinblade Assassins
 * {3}{B}{G}
 * Creature — Elf Assassin
 * 5/4
 *
 * At the beginning of your end step, if a creature died this turn, draw a card.
 */
val TwinbladeAssassins = card("Twinblade Assassins") {
    manaCost = "{3}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Elf Assassin"
    power = 5
    toughness = 4
    oracleText = "At the beginning of your end step, if a creature died this turn, draw a card."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        interveningIf = Conditions.CreatureDiedThisTurn
        effect = Effects.DrawCards(1)
        description = "At the beginning of your end step, if a creature died this turn, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "226"
        artist = "Campbell White"
        flavorText = "Rumors swirl of a pair of deadly Golgari assassins known only as the Left " +
            "and Right Hands of the Ochran."
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d387b19-a0a7-45c0-b1e9-71ca55fb4adc.jpg?1783930659"
    }
}
