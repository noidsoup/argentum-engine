package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Evangel of Heliod reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in THS's `cards/` package (the card's earliest real printing).
 */
val EvangelOfHeliodReprint = Printing(
    oracleId = "4fbe1c02-f5bf-47c7-80de-09a32d01e5db",
    name = "Evangel of Heliod",
    setCode = "KHC",
    collectorNumber = "23",
    scryfallId = "ccc00c82-7c2a-4699-8058-e57f690ece96",
    artist = "Nils Hamm",
    imageUri = "https://cards.scryfall.io/normal/front/c/c/ccc00c82-7c2a-4699-8058-e57f690ece96.jpg?1783928332",
    releaseDate = "2021-02-05",
    rarity = Rarity.UNCOMMON,
)
