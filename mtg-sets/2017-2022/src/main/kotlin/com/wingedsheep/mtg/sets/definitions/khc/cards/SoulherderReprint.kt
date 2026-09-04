package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Soulherder reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Modern Horizons (`com.wingedsheep.mtg.sets.definitions.mh1.cards.Soulherder`).
 */
val SoulherderReprint = Printing(
    oracleId = "92019547-f6db-4ea6-8356-d0a90ace5662",
    name = "Soulherder",
    setCode = "KHC",
    collectorNumber = "93",
    scryfallId = "50bc0f5b-7421-45b9-af85-86dd9821b7d8",
    artist = "Seb McKinnon",
    imageUri = "https://cards.scryfall.io/normal/front/5/0/50bc0f5b-7421-45b9-af85-86dd9821b7d8.jpg?1783928301",
    releaseDate = "2021-02-05",
    rarity = Rarity.UNCOMMON,
)
