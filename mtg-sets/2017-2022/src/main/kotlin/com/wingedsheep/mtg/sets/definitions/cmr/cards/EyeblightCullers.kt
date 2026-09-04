package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Eyeblight Cullers
 * {4}{B}
 * Creature — Elf Warrior
 * 3/3
 *
 * When this creature dies, create three 1/1 green Elf Warrior creature tokens, then mill three cards.
 */
val EyeblightCullers = card("Eyeblight Cullers") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elf Warrior"
    oracleText = "When this creature dies, create three 1/1 green Elf Warrior creature tokens, " +
        "then mill three cards. (Put the top three cards of your library into your graveyard.)"
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.Composite(
            Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Elf", "Warrior"),
                count = 3,
                imageUri = "https://cards.scryfall.io/normal/front/f/f/ff2aea59-c5f1-4927-8534-2215ef79bec7.jpg?1783928588",
            ),
            Patterns.Library.mill(3),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "124"
        artist = "Randy Vargas"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da777e75-6f1f-4e07-a5ec-e25454a0636c.jpg?1783928840"
    }
}
