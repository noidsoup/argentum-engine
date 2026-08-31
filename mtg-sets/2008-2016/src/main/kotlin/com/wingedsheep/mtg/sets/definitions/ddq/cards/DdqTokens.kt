package com.wingedsheep.mtg.sets.definitions.ddq.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/** DDQ #77 — 4/4 white Angel creature token with flying. */
val AngelToken = card("Angel") {
    colorIdentity = "W"
    typeLine = "Token Creature — Angel"
    oracleText = "Flying"
    power = 4
    toughness = 4
    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Winona Nelson"
        imageUri =
            "https://cards.scryfall.io/normal/front/8/e/8e3a583e-2310-4284-ac44-fd28c72ec11b.jpg?1783937832"
    }
}

/** DDQ #78 — 1/1 white Human creature token. */
val HumanToken = card("Human") {
    colorIdentity = "W"
    typeLine = "Token Creature — Human"
    power = 1
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "78"
        artist = "John Stanko"
        imageUri =
            "https://cards.scryfall.io/normal/front/1/5/15a620da-5056-4582-8da5-2c955c3f4c0d.jpg?1783937832"
    }
}

/** DDQ #79 — 1/1 white Spirit creature token with flying. */
val SpiritToken = card("Spirit") {
    colorIdentity = "W"
    typeLine = "Token Creature — Spirit"
    oracleText = "Flying"
    power = 1
    toughness = 1
    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "79"
        artist = "Kev Walker"
        imageUri =
            "https://cards.scryfall.io/normal/front/b/3/b38c8153-ded1-499f-929a-b7bc8a09cd5a.jpg?1783937831"
    }
}

/** DDQ #80 — 2/2 black Zombie creature token. */
val ZombieToken = card("Zombie") {
    colorIdentity = "B"
    typeLine = "Token Creature — Zombie"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "80"
        artist = "Lucas Graciano"
        imageUri =
            "https://cards.scryfall.io/normal/front/7/c/7c60e495-8fb7-43bb-b11d-52882c0246bc.jpg?1783937831"
    }
}
