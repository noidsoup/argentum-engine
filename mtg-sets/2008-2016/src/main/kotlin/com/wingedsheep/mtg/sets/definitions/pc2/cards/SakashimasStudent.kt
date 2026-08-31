package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersAsCopy

/**
 * Sakashima's Student
 * {2}{U}{U}
 * Creature — Human Ninja
 * 0/0
 *
 * Ninjutsu {1}{U} ({1}{U}, Return an unblocked attacker you control to hand: Put this card onto
 * the battlefield from your hand tapped and attacking.)
 * You may have this creature enter as a copy of any creature on the battlefield, except it's a
 * Ninja in addition to its other creature types.
 */
val SakashimasStudent = card("Sakashima's Student") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Ninja"
    power = 0
    toughness = 0
    oracleText = "Ninjutsu {1}{U} ({1}{U}, Return an unblocked attacker you control to hand: Put " +
        "this card onto the battlefield from your hand tapped and attacking.)\n" +
        "You may have this creature enter as a copy of any creature on the battlefield, except " +
        "it's a Ninja in addition to its other creature types."

    ninjutsu("{1}{U}")

    replacementEffect(
        EntersAsCopy(
            optional = true,
            additionalSubtypes = listOf("Ninja"),
        ),
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "24"
        artist = "Brian Snõddy"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c3d4d71-c750-4146-80fb-7cfd2427c62f.jpg?1783940629"
    }
}
