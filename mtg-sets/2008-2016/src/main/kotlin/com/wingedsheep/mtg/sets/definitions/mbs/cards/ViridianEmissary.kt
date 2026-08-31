package com.wingedsheep.mtg.sets.definitions.mbs.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Viridian Emissary
 * {1}{G}
 * Creature — Phyrexian Elf Scout
 * 2/1
 *
 * When this creature dies, you may search your library for a basic land card, put it onto the
 * battlefield tapped, then shuffle.
 *
 * The dies trigger is untargeted, so `optional = true` carries the whole "you may" — declining skips
 * the search (and the shuffle) entirely.
 */
val ViridianEmissary = card("Viridian Emissary") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Phyrexian Elf Scout"
    power = 2
    toughness = 1
    oracleText = "When this creature dies, you may search your library for a basic land card, put " +
        "it onto the battlefield tapped, then shuffle."

    triggeredAbility {
        trigger = Triggers.Dies
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/1/2/129fa334-f561-4fbd-9f51-2fa044b674e1.jpg?1783941372"
    }
}
