package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBlock

/**
 * Tormented Soul
 * {B}
 * Creature — Spirit
 * 1/1
 *
 * This creature can't block and can't be blocked.
 */
val TormentedSoul = card("Tormented Soul") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 1
    oracleText = "This creature can't block and can't be blocked."

    staticAbility {
        ability = CantBlock()
    }
    flags(AbilityFlag.CANT_BE_BLOCKED)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Karl Kopinski"
        flavorText = "Those who raged most bitterly at the world in life are cursed to roam the nether realms in death."
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2699c42-99bb-4b5a-82ec-9c6424c14ec1.jpg?1783941076"
    }
}
