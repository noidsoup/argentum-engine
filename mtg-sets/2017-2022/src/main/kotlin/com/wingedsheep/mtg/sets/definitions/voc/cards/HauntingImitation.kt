package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Haunting Imitation
 * {2}{U}
 * Sorcery
 *
 * Each player reveals the top card of their library. For each creature card revealed this way,
 * create a token that's a copy of that card, except it's 1/1, it's a Spirit in addition to its
 * other types, and it has flying. If no creature cards were revealed this way, return Haunting
 * Imitation to its owner's hand.
 */
val HauntingImitation = card("Haunting Imitation") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Each player reveals the top card of their library. For each creature card " +
        "revealed this way, create a token that's a copy of that card, except it's 1/1, it's " +
        "a Spirit in addition to its other types, and it has flying. If no creature cards were " +
        "revealed this way, return Haunting Imitation to its owner's hand."

    spell {
        effect = Patterns.Mechanic.eachPlayerRevealTopCreaturesCreateCopiesElseReturnSource()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "13"
        artist = "Liiga Smilshkalne"
        imageUri = "https://cards.scryfall.io/normal/front/8/4/8418fd80-2c92-40d4-87de-0c149f6815bc.jpg?1783925003"
    }
}
