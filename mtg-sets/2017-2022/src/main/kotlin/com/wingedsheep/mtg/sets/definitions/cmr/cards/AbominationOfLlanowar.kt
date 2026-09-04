package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Abomination of Llanowar
 * {1}{B}{G}
 * Legendary Creature — Elf Horror
 * star/star P/T
 * Vigilance; menace
 * Abomination of Llanowar's power and toughness are each equal to the number of Elves you control
 * plus the number of Elf cards in your graveyard.
 *
 * The elf tally matches [GloomRipper]'s X: battlefield Elves use [GameObjectFilter.Permanent] so
 * noncreature Elf permanents count, and the graveyard half counts every Elf card there — including
 * this one once it has died.
 */
val AbominationOfLlanowar = card("Abomination of Llanowar") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Elf Horror"
    oracleText = "Vigilance; menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Abomination of Llanowar's power and toughness are each equal to the number of Elves you " +
        "control plus the number of Elf cards in your graveyard."

    dynamicStats(
        DynamicAmount.Add(
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Permanent.withSubtype(Subtype.ELF)).count(),
            DynamicAmounts.zone(Player.You, Zone.GRAVEYARD, GameObjectFilter.Any.withSubtype(Subtype.ELF)).count(),
        ),
    )

    keywords(Keyword.VIGILANCE, Keyword.MENACE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "265"
        artist = "Vincent Proce"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/567dbc64-4d59-4bab-a551-08fc66c085fa.jpg?1783928779"
        ruling("2020-11-20", "Because Abomination of Llanowar is an Elf, it counts itself when determining its power and toughness.")
    }
}
