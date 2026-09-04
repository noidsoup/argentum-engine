package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Opal Palace reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Commander 2013 (C13), `com.wingedsheep.mtg.sets.definitions.c13.cards.OpalPalace`.
 */
val OpalPalaceReprint = Printing(
    oracleId = "aa6723a2-75da-49f5-a1ba-cbfa82c55301",
    name = "Opal Palace",
    setCode = "KHC",
    collectorNumber = "116",
    scryfallId = "677f7ab0-27eb-4dcd-a38d-2a880731eaa3",
    artist = "Andreas Rocha",
    imageUri = "https://cards.scryfall.io/normal/front/6/7/677f7ab0-27eb-4dcd-a38d-2a880731eaa3.jpg?1783928290",
    releaseDate = "2021-02-05",
    rarity = Rarity.COMMON,
)
