package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Arcane Artisan reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in BBD's `cards/` package (the card's earliest real printing).
 */
val ArcaneArtisanReprint = Printing(
    oracleId = "7dbf5f59-1246-4156-bb21-090cfddd9111",
    name = "Arcane Artisan",
    setCode = "KHC",
    collectorNumber = "36",
    scryfallId = "ba946cfb-a729-406b-be35-0defaf95283e",
    artist = "Tommy Arnold",
    imageUri = "https://cards.scryfall.io/normal/front/b/a/ba946cfb-a729-406b-be35-0defaf95283e.jpg?1783928326",
    releaseDate = "2021-02-05",
    rarity = Rarity.MYTHIC,
)
