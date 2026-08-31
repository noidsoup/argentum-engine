package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.dsl.basicLand

/**
 * Planechase 2012 basic lands (one art variant per type in this set).
 */

val Plains132 = basicLand("Plains") {
    collectorNumber = "132"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/a/d/add06510-2c8f-43c9-96af-25644aa665c3.jpg"
}

val Island137 = basicLand("Island") {
    collectorNumber = "137"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/9/5/95d6fbe1-ec26-4765-a3a9-0d1a0d06ce35.jpg"
}

val Swamp142 = basicLand("Swamp") {
    collectorNumber = "142"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/d/c/dc1e540d-6833-4e74-806f-a3894f73c347.jpg"
}

val Mountain147 = basicLand("Mountain") {
    collectorNumber = "147"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/6/1/61d491c3-cc81-4aeb-bd60-4cf441cb6036.jpg"
}

val Forest151 = basicLand("Forest") {
    collectorNumber = "151"
    artist = "John Avon"
    imageUri = "https://cards.scryfall.io/normal/front/b/2/b2e7696e-7514-4c50-839d-cb5f61c52978.jpg"
}

val Planechase2012BasicLands = listOf(
    Plains132,
    Island137,
    Swamp142,
    Mountain147,
    Forest151,
)
