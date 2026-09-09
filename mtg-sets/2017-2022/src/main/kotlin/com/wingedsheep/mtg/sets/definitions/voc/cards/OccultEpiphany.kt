package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Occult Epiphany
 * {X}{U}
 * Instant
 *
 * Draw X cards, then discard X cards. Create a 1/1 white Spirit creature token with flying for
 * each card type among cards discarded this way.
 *
 * X is [DynamicAmount.XValue] for both draw and discard. The token count uses
 * [DynamicAmounts.distinctCardTypesIn] over the `discarded` pipeline collection that
 * [Patterns.Hand.discardCards] publishes — the same idiom as Kefka, Court Mage's payoff.
 */
val OccultEpiphany = card("Occult Epiphany") {
    manaCost = "{X}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Draw X cards, then discard X cards. Create a 1/1 white Spirit creature token " +
        "with flying for each card type among cards discarded this way."

    spell {
        effect = Effects.Composite(
            listOf(
                Effects.DrawCards(DynamicAmount.XValue),
                Patterns.Hand.discardCards(DynamicAmount.XValue),
                Effects.CreateToken(
                    count = DynamicAmounts.distinctCardTypesIn("discarded"),
                    power = 1,
                    toughness = 1,
                    colors = setOf(Color.WHITE),
                    creatureTypes = setOf("Spirit"),
                    keywords = setOf(Keyword.FLYING),
                    imageUri = "https://cards.scryfall.io/normal/front/8/3/83497714-97ae-4846-8096-f7f1524f0e09.jpg?1783924702",
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "14"
        artist = "Jason Rainville"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/6920c895-bc98-4871-a53f-219fa27a74e5.jpg?1783925004"
    }
}
