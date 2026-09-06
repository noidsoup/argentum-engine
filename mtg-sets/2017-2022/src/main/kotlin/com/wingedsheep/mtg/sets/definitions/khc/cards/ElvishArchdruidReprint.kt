package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Elvish Archdruid reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in M10's `cards/` package (the card's earliest real printing).
 */
val ElvishArchdruidReprint = Printing(
    oracleId = "6e2c2423-d854-4478-99e6-64f29851f026",
    name = "Elvish Archdruid",
    setCode = "KHC",
    collectorNumber = "57",
    scryfallId = "4d3fffa5-50ec-4502-9f03-bd9618f1771e",
    artist = "Raymond Swanland",
    imageUri = "https://cards.scryfall.io/normal/front/4/d/4d3fffa5-50ec-4502-9f03-bd9618f1771e.jpg?1783928316",
    releaseDate = "2021-02-05",
    rarity = Rarity.RARE,
)
