package com.wingedsheep.mtg.sets.definitions.pz2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Inspired Sphinx
 * {5}{U}{U}
 * Creature — Sphinx
 * 5/5
 *
 * Flying
 * When this creature enters, draw cards equal to the number of opponents you have.
 * {3}{U}: Create a 1/1 colorless Thopter artifact creature token with flying.
 */
val InspiredSphinx = card("Inspired Sphinx") {
    manaCost = "{5}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx"
    power = 5
    toughness = 5
    oracleText = "Flying\n" +
        "When this creature enters, draw cards equal to the number of opponents you have.\n" +
        "{3}{U}: Create a 1/1 colorless Thopter artifact creature token with flying."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(DynamicAmount.PlayerCount(Player.EachOpponent))
    }

    activatedAbility {
        cost = AbilityCost.Composite(listOf(Costs.Mana(ManaCost.parse("{3}{U}"))))
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            creatureTypes = setOf("Thopter"),
            keywords = setOf(Keyword.FLYING),
            artifactToken = true,
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "70779"
        artist = "Jesper Ejsing"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/03056f18-96a4-49d6-bc94-147f11ad6ae7.jpg?1783933916"
    }
}
