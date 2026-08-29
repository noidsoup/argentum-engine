package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dragonlair Spider
 * {2}{R}{R}{G}{G}
 * Creature — Spider
 * 5/6
 *
 * Reach
 * Whenever an opponent casts a spell, create a 1/1 green Insect creature token.
 */
val DragonlairSpider = card("Dragonlair Spider") {
    manaCost = "{2}{R}{R}{G}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Spider"
    oracleText = "Reach\nWhenever an opponent casts a spell, create a 1/1 green Insect creature token."
    power = 5
    toughness = 6

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.OpponentCastsSpell
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Insect"),
            imageUri = "https://cards.scryfall.io/normal/front/a/a/aa47df37-f246-4f80-a944-008cdf347dad.jpg?1561757793",
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "87"
        artist = "Carl Critchlow"
        flavorText = "Swarms thrive in its nest, feeding on leathery bits of discarded wing."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56a23047-04c8-4f37-8296-f489370859aa.jpg?1783940602"
    }
}
