package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Timberwatch Elf reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in LGN's `cards/` package (the card's earliest real printing).
 */
val TimberwatchElfReprint = Printing(
    oracleId = "50cee3ac-cba0-4abb-babf-de1928b1590e",
    name = "Timberwatch Elf",
    setCode = "KHC",
    collectorNumber = "76",
    scryfallId = "38807f17-1cf2-4736-ad10-df6c8b1a9f55",
    artist = "Yohann Schepacz",
    imageUri = "https://cards.scryfall.io/normal/front/3/8/38807f17-1cf2-4736-ad10-df6c8b1a9f55.jpg?1783928309",
    releaseDate = "2021-02-05",
    rarity = Rarity.COMMON,
)
