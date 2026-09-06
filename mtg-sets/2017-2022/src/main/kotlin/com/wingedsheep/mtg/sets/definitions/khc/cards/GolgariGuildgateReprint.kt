package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Golgari Guildgate reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in RTR's `cards/` package (the card's earliest real printing).
 */
val GolgariGuildgateReprint = Printing(
    oracleId = "fa2da325-6859-45bb-b185-35526b01bcc1",
    name = "Golgari Guildgate",
    setCode = "KHC",
    collectorNumber = "111",
    scryfallId = "7e6228ce-0d36-4c93-80e0-949ffb9fe15e",
    artist = "Eytan Zana",
    imageUri = "https://cards.scryfall.io/normal/front/7/e/7e6228ce-0d36-4c93-80e0-949ffb9fe15e.jpg?1783928294",
    releaseDate = "2021-02-05",
    rarity = Rarity.COMMON,
)
