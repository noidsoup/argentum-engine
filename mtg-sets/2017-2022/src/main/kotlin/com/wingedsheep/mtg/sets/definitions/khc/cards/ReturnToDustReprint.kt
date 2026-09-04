package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Return to Dust reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Time Spiral (`com.wingedsheep.mtg.sets.definitions.tsp.cards.ReturnToDust`).
 */
val ReturnToDustReprint = Printing(
    oracleId = "3029df1d-d02a-4fed-8ab4-000a2096f823",
    name = "Return to Dust",
    setCode = "KHC",
    collectorNumber = "32",
    scryfallId = "0034ed95-a296-44c1-a084-e03c57c1865f",
    artist = "Wayne Reynolds",
    imageUri = "https://cards.scryfall.io/normal/front/0/0/0034ed95-a296-44c1-a084-e03c57c1865f.jpg?1783928330",
    releaseDate = "2021-02-05",
    rarity = Rarity.UNCOMMON,
)
