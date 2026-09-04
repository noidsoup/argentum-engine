package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Mistmeadow Witch reprint in KHC. Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in
 * Shadowmoor (SHM), `com.wingedsheep.mtg.sets.definitions.shm.cards.MistmeadowWitch`.
 */
val MistmeadowWitchReprint = Printing(
    oracleId = "38e274e2-bd04-48de-a1df-44f0ee987ba8",
    name = "Mistmeadow Witch",
    setCode = "KHC",
    collectorNumber = "88",
    scryfallId = "570afcb8-8cf0-4946-863a-be19c0a5e110",
    artist = "Greg Staples",
    imageUri = "https://cards.scryfall.io/normal/front/5/7/570afcb8-8cf0-4946-863a-be19c0a5e110.jpg?1783928305",
    releaseDate = "2021-02-05",
    rarity = Rarity.UNCOMMON,
)
