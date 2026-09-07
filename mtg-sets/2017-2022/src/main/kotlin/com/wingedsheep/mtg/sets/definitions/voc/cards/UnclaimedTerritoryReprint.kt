package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Unclaimed Territory reprint in Innistrad: Crimson Vow Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `xln` `cards/` package;
 * this file contributes only per-printing presentation data.
 */
val UnclaimedTerritoryReprint = Printing(
    oracleId = "584b15f2-6ae9-413a-8b8d-9244dbea4878",
    name = "Unclaimed Territory",
    setCode = "VOC",
    collectorNumber = "188",
    scryfallId = "6037414e-8fa6-48df-bcd6-f5be022bf4af",
    artist = "Dimitar Marinski",
    imageUri = "https://cards.scryfall.io/normal/front/6/0/6037414e-8fa6-48df-bcd6-f5be022bf4af.jpg?1783924929",
    releaseDate = "2021-11-19",
    rarity = Rarity.UNCOMMON,
)
