package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Curse of the Swine reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in THS's `cards/` package (the card's earliest real printing).
 */
val CurseOfTheSwineReprint = Printing(
    oracleId = "5669ea7c-c4fc-494c-896b-4bce9b494817",
    name = "Curse of the Swine",
    setCode = "KHC",
    collectorNumber = "37",
    scryfallId = "227c0e9c-4f8f-4402-bdd1-dd744dc49927",
    artist = "James Ryman",
    imageUri = "https://cards.scryfall.io/normal/front/2/2/227c0e9c-4f8f-4402-bdd1-dd744dc49927.jpg?1783928325",
    releaseDate = "2021-02-05",
    rarity = Rarity.RARE,
)
