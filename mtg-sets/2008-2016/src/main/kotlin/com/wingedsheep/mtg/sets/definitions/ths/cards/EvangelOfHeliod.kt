package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Evangel of Heliod
 * {4}{W}{W}
 * Creature — Human Cleric
 * 1/3
 *
 * When this creature enters, create a number of 1/1 white Soldier creature tokens equal to your
 * devotion to white.
 *
 * Devotion is read at resolution via [DynamicAmounts.devotionTo] — the Evangel is already on the
 * battlefield, so its own {W}{W} counts along with every other permanent you control.
 */
val EvangelOfHeliod = card("Evangel of Heliod") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "When this creature enters, create a number of 1/1 white Soldier creature tokens " +
        "equal to your devotion to white. (Each {W} in the mana costs of permanents you control " +
        "counts toward your devotion to white.)"
    power = 1
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            count = DynamicAmounts.devotionTo(Color.WHITE),
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Soldier"),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "11"
        artist = "Nils Hamm"
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bb914a85-3755-4663-8309-6f6d0319262e.jpg?1783939817"
    }
}
