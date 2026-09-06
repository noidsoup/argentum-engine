package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Storm Herd reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in GPT's `cards/` package (the card's earliest real printing).
 */
val StormHerdReprint = Printing(
    oracleId = "30aa362b-7ab1-486e-9802-1171e0dc2416",
    name = "Storm Herd",
    setCode = "KHC",
    collectorNumber = "33",
    scryfallId = "2345331c-a14f-4b4b-a9a4-88c5beb48242",
    artist = "Jim Nelson",
    imageUri = "https://cards.scryfall.io/normal/front/2/3/2345331c-a14f-4b4b-a9a4-88c5beb48242.jpg?1783928328",
    releaseDate = "2021-02-05",
    rarity = Rarity.RARE,
)
