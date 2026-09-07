package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Field of Souls reprint in Innistrad: Crimson Vow Commander. The canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives in the `tmp` `cards/` package;
 * this file contributes only per-printing presentation data.
 */
val FieldOfSoulsReprint = Printing(
    oracleId = "4d7a5b14-8fce-41f2-a0d5-fff3d15f41f6",
    name = "Field of Souls",
    setCode = "VOC",
    collectorNumber = "86",
    scryfallId = "020dc072-96e2-4923-b9c9-1791a0732c29",
    artist = "Richard Kane Ferguson",
    imageUri = "https://cards.scryfall.io/normal/front/0/2/020dc072-96e2-4923-b9c9-1791a0732c29.jpg?1783924975",
    releaseDate = "2021-11-19",
    rarity = Rarity.UNCOMMON,
)
