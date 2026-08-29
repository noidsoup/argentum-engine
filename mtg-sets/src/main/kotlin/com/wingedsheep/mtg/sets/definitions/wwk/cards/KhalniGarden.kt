package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Khalni Garden
 * Land
 *
 * This land enters tapped.
 * When this land enters, create a 0/1 green Plant creature token.
 * {T}: Add {G}.
 */
val KhalniGarden = card("Khalni Garden") {
    manaCost = ""
    colorIdentity = "G"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "When this land enters, create a 0/1 green Plant creature token.\n" +
        "{T}: Add {G}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 0,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Plant"),
            imageUri = "https://cards.scryfall.io/normal/front/c/1/c1424e8d-1f96-44af-9382-c337b6695ddf.jpg?1783942069",
        )
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Ryan Pancoast"
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1cc6a5e6-0b73-4488-8954-4b168ce7106d.jpg?1783942036"
    }
}
