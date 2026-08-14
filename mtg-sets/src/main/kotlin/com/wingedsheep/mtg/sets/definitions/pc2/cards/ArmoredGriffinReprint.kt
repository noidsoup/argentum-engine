package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Armored Griffin reprint in PC2. Canonical CardDefinition lives in P02 (Portal Second Age, its
 * earliest real printing). Note the PC2 art is by a different artist than the P02 original.
 */
val ArmoredGriffinReprint = Printing(
    oracleId = "ad3d6003-66d3-486d-aa54-ddce1adb5ff1",
    name = "Armored Griffin",
    setCode = "PC2",
    collectorNumber = "1",
    scryfallId = "0f638ffc-20b3-4e8c-8f0a-0d034902bddd",
    artist = "Brad Rigney",
    imageUri = "https://cards.scryfall.io/normal/front/0/f/0f638ffc-20b3-4e8c-8f0a-0d034902bddd.jpg?1783940640",
    releaseDate = "2012-06-01",
    rarity = Rarity.UNCOMMON,
)
