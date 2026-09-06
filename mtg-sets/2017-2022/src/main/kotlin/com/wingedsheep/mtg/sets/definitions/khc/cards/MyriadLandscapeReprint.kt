package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Myriad Landscape reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in C14's `cards/` package (the card's earliest real printing).
 */
val MyriadLandscapeReprint = Printing(
    oracleId = "2549bc57-9ffb-4053-9f10-f2a5f792b845",
    name = "Myriad Landscape",
    setCode = "KHC",
    collectorNumber = "115",
    scryfallId = "4544593c-c155-436f-a74b-eea3e854ad1d",
    artist = "Richard Wright",
    imageUri = "https://cards.scryfall.io/normal/front/4/5/4544593c-c155-436f-a74b-eea3e854ad1d.jpg?1783928291",
    releaseDate = "2021-02-05",
    rarity = Rarity.UNCOMMON,
)
