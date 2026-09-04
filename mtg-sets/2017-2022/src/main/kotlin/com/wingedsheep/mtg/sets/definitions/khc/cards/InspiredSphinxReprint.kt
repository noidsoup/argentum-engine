package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Inspired Sphinx reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Treasure Chest (`com.wingedsheep.mtg.sets.definitions.pz2.cards.InspiredSphinx`).
 */
val InspiredSphinxReprint = Printing(
    oracleId = "61cceb14-9568-4a5e-9b56-07afa6407df2",
    name = "Inspired Sphinx",
    setCode = "KHC",
    collectorNumber = "40",
    scryfallId = "7f58d798-9b99-45a9-9015-f689145018a8",
    artist = "Jesper Ejsing",
    imageUri = "https://cards.scryfall.io/normal/front/7/f/7f58d798-9b99-45a9-9015-f689145018a8.jpg?1783928324",
    releaseDate = "2021-02-05",
    rarity = Rarity.MYTHIC,
)
