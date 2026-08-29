package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Liliana's Specter
 * {1}{B}{B}
 * Creature — Specter
 * 2/2
 * Flying
 * When this creature enters, each opponent discards a card.
 */
val LilianasSpecter = card("Liliana's Specter") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Specter"
    oracleText = "Flying\nWhen this creature enters, each opponent discards a card."
    power = 2
    toughness = 2
    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.eachOpponentDiscards(1)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "104"
        artist = "Steve Argyle"
        flavorText = "\"I've seen your nightmares, and you're not afraid of anything. Except me.\" —Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33122581-39fd-44a0-b928-f73e39a0c0f1.jpg"
    }
}
