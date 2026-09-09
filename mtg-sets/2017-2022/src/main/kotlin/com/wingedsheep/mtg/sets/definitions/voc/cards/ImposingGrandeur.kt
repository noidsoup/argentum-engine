package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Imposing Grandeur
 * {4}{R}
 * Sorcery
 *
 * Each player may discard their hand and draw cards equal to the greatest mana value of a
 * commander they own on the battlefield or in the command zone.
 */
val ImposingGrandeur = card("Imposing Grandeur") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText =
        "Each player may discard their hand and draw cards equal to the greatest mana value of a " +
            "commander they own on the battlefield or in the command zone."

    spell {
        effect = Patterns.Hand.eachPlayerMayDiscardHandAndDraw(
            DynamicAmounts.greatestOwnedCommanderManaValue(),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "24"
        artist = "Mila Pesic"
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d236055-d524-4312-9c6f-eddb34703e3e.jpg?1783925000"
    }
}
