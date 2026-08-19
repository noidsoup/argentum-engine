package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Brindle Shoat
 * {1}{G}
 * Creature — Boar
 * 1/1
 *
 * When this creature dies, create a 3/3 green Boar creature token.
 */
val BrindleShoat = card("Brindle Shoat") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Boar"
    power = 1
    toughness = 1
    oracleText = "When this creature dies, create a 3/3 green Boar creature token."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Boar"),
            imageUri = "https://cards.scryfall.io/normal/front/8/a/8a911fad-7af3-4bc4-bafc-cac2d5f18f22.jpg?1783921674",
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "60"
        artist = "Steven Belledin"
        flavorText = "Hunters lure the stripling boar into the open, hoping to trap greater prey."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e40569c-640b-4a1a-a586-bde37de5591f.jpg?1783940614"
    }
}
