package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Banishing Light reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in JOU's `cards/` package (the card's earliest real printing).
 */
val BanishingLightReprint = Printing(
    oracleId = "f28b21a6-f7ce-437a-8c5b-0423cb55cefb",
    name = "Banishing Light",
    setCode = "KHC",
    collectorNumber = "19",
    scryfallId = "0858ab5a-930c-44f0-9621-4dd634cf39c6",
    artist = "Willian Murai",
    imageUri = "https://cards.scryfall.io/normal/front/0/8/0858ab5a-930c-44f0-9621-4dd634cf39c6.jpg?1783928333",
    releaseDate = "2021-02-05",
    rarity = Rarity.UNCOMMON,
)
