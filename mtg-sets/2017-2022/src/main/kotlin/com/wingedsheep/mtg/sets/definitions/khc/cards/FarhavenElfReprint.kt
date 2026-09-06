package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Farhaven Elf reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in SHM's `cards/` package (the card's earliest real printing).
 */
val FarhavenElfReprint = Printing(
    oracleId = "4ce2357f-93e6-40ca-beca-8f4e15adc464",
    name = "Farhaven Elf",
    setCode = "KHC",
    collectorNumber = "62",
    scryfallId = "320d0a4c-c556-4468-b934-bfdf17961a53",
    artist = "Brandon Kitkouski",
    imageUri = "https://cards.scryfall.io/normal/front/3/2/320d0a4c-c556-4468-b934-bfdf17961a53.jpg?1783928316",
    releaseDate = "2021-02-05",
    rarity = Rarity.COMMON,
)
