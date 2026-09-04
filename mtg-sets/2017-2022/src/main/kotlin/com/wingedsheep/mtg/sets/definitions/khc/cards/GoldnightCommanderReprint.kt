package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Goldnight Commander reprint in KHC. Canonical CardDefinition lives in Avacyn Restored (its earliest
 * real printing), `com.wingedsheep.mtg.sets.definitions.avr.cards.GoldnightCommander`.
 */
val GoldnightCommanderReprint = Printing(
    oracleId = "3dc51dfc-7a60-41bf-b944-6afad6b06c22",
    name = "Goldnight Commander",
    setCode = "KHC",
    collectorNumber = "27",
    scryfallId = "80be2fea-3fe6-477e-bcb8-63f441d6cfc1",
    artist = "Chris Rahn",
    imageUri = "https://cards.scryfall.io/normal/front/8/0/80be2fea-3fe6-477e-bcb8-63f441d6cfc1.jpg?1783928330",
    releaseDate = "2021-02-05",
    rarity = Rarity.UNCOMMON,
)
