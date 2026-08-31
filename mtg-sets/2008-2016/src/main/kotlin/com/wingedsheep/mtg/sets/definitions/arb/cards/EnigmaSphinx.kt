package com.wingedsheep.mtg.sets.definitions.arb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Enigma Sphinx
 * {4}{W}{U}{B}
 * Artifact Creature — Sphinx
 * 5/4
 *
 * Flying
 * When this creature is put into your graveyard from the battlefield, put it into your library
 * third from the top.
 * Cascade
 */
val EnigmaSphinx = card("Enigma Sphinx") {
    manaCost = "{4}{W}{U}{B}"
    colorIdentity = "WUB"
    typeLine = "Artifact Creature — Sphinx"
    power = 5
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature is put into your graveyard from the battlefield, put it into your " +
        "library third from the top.\n" +
        "Cascade (When you cast this spell, exile cards from the top of your library until you " +
        "exile a nonland card that costs less. You may cast it without paying its mana cost. Put " +
        "the exiled cards on the bottom of your library in a random order.)"

    keywords(Keyword.FLYING, Keyword.CASCADE)

    triggeredAbility {
        trigger = Triggers.PutIntoGraveyardFromBattlefield
        effect = Effects.PutIntoLibraryNthFromTop(EffectTarget.Self, positionFromTop = 2)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "106"
        artist = "Chris Rahn"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7fcfe9da-4c82-4476-8e83-24297fe5c176.jpg?1783942533"
    }
}
