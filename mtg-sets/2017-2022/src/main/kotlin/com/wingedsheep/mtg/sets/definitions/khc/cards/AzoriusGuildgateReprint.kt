package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Azorius Guildgate reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition]
 * lives in RTR's `cards/` package (the card's earliest real printing).
 */
val AzoriusGuildgateReprint = Printing(
    oracleId = "ad1712d8-809f-410c-8b91-ffe6fb8a69a1",
    name = "Azorius Guildgate",
    setCode = "KHC",
    collectorNumber = "107",
    scryfallId = "569b53fb-8776-4301-9ed6-f7eabb08cebf",
    artist = "Drew Baker",
    imageUri = "https://cards.scryfall.io/normal/front/5/6/569b53fb-8776-4301-9ed6-f7eabb08cebf.jpg?1783928296",
    releaseDate = "2021-02-05",
    rarity = Rarity.COMMON,
)
