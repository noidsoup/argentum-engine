package com.wingedsheep.mtg.sets.definitions.bok.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Oyobi, Who Split the Heavens
 * {6}{W}
 * Legendary Creature — Spirit
 * 3/6
 *
 * Flying
 * Whenever you cast a Spirit or Arcane spell, create a 3/3 white Spirit creature token with flying.
 *
 * The shared Kamigawa "Whenever you cast a Spirit or Arcane spell" trigger — [Triggers.youCastSpell]
 * over a homogeneous OR of the two subtype filters ([SireOfTheStorm] shape).
 */
val OyobiWhoSplitTheHeavens = card("Oyobi, Who Split the Heavens") {
    manaCost = "{6}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Spirit"
    oracleText = "Flying\n" +
        "Whenever you cast a Spirit or Arcane spell, create a 3/3 white Spirit creature token with flying."
    power = 3
    toughness = 6

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane"),
        )
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/e/6/e6e361f5-a662-495f-a8e5-e4a647833af5.jpg?1783924702",
        )
        description = "Whenever you cast a Spirit or Arcane spell, create a 3/3 white Spirit " +
            "creature token with flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "18"
        artist = "Christopher Moeller"
        flavorText = "Her angry call split the sky. From that rift descended her champions."
        imageUri = "https://cards.scryfall.io/normal/front/3/1/313bd276-2f69-447e-a2b1-240cf839614a.jpg?1783944211"
    }
}
