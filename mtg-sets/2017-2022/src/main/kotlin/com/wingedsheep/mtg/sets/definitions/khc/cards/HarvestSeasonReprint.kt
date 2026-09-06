package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Harvest Season reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in AKH's `cards/` package (the card's earliest real printing).
 */
val HarvestSeasonReprint = Printing(
    oracleId = "ec6d52f9-1c8f-48da-951c-be8d7813c7be",
    name = "Harvest Season",
    setCode = "KHC",
    collectorNumber = "63",
    scryfallId = "326b5fad-bb8d-4019-84a8-1a319a14962e",
    artist = "Shreya Shetty",
    imageUri = "https://cards.scryfall.io/normal/front/3/2/326b5fad-bb8d-4019-84a8-1a319a14962e.jpg?1783928314",
    releaseDate = "2021-02-05",
    rarity = Rarity.RARE,
)
