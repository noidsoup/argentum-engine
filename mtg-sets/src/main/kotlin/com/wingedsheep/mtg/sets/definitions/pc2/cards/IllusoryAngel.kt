package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Illusory Angel
 * {2}{U}
 * Creature — Angel Illusion
 * 4/4
 *
 * Cast this spell only if you've cast another spell this turn.
 * Flying
 */
val IllusoryAngel = card("Illusory Angel") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Angel Illusion"
    power = 4
    toughness = 4
    oracleText = "Cast this spell only if you've cast another spell this turn.\nFlying"

    keywords(Keyword.FLYING)

    spell {
        castOnlyIf(Conditions.YouCastSpellsThisTurn(atLeast = 1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "19"
        artist = "Allen Williams"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f7c0cb1-1102-4125-b9cd-a9a0478e3cb2.jpg?1783940631"
    }
}
