package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Empyrean Eagle reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in M20's `cards/` package (the card's earliest real printing).
 */
val EmpyreanEagleReprint = Printing(
    oracleId = "270d14b2-07bc-46bc-918f-658102265ccf",
    name = "Empyrean Eagle",
    setCode = "KHC",
    collectorNumber = "85",
    scryfallId = "a2a0f40e-12de-4ee8-8b1e-6989dbfa9a4d",
    artist = "Ryan Yee",
    imageUri = "https://cards.scryfall.io/normal/front/a/2/a2a0f40e-12de-4ee8-8b1e-6989dbfa9a4d.jpg?1783928305",
    releaseDate = "2021-02-05",
    rarity = Rarity.UNCOMMON,
)
