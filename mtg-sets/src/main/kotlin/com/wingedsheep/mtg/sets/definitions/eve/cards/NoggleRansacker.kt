package com.wingedsheep.mtg.sets.definitions.eve.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Noggle Ransacker
 * {2}{U/R}
 * Creature — Noggle Rogue
 * 2/1
 * When this creature enters, each player draws two cards, then discards a card at random.
 */
val NoggleRansacker = card("Noggle Ransacker") {
    manaCost = "{2}{U/R}"
    colorIdentity = "UR"
    typeLine = "Creature — Noggle Rogue"
    oracleText =
        "When this creature enters, each player draws two cards, then discards a card at random."
    power = 2
    toughness = 1
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CompositeEffect(
            listOf(
                Effects.ForEachPlayer(
                    players = Player.Each,
                    effects = listOf(DrawCardsEffect(2)),
                ),
                Effects.ForEachPlayer(
                    players = Player.Each,
                    effects = listOf(Patterns.Hand.discardRandom(1)),
                ),
            ),
        )
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "109"
        artist = "Alex Horley-Orlandelli"
        flavorText =
            "Noggles live purely by what they can scavenge. There is not a single thing a noggle " +
                "eats, wears, or uses that did not once belong to another."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e6dbe4e2-77f3-4764-b9fb-b555a1228bf3.jpg"
    }
}
