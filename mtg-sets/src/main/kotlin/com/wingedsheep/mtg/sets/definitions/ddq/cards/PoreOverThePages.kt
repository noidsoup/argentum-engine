package com.wingedsheep.mtg.sets.definitions.ddq.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Pore Over the Pages
 * {3}{U}{U}
 * Sorcery
 * Draw three cards, untap up to two lands, then discard a card.
 *
 * Canonical printing is DDQ (pre-SOI).
 */
val PoreOverThePages = card("Pore Over the Pages") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Draw three cards, untap up to two lands, then discard a card."

    spell {
        target(
            "up to two lands",
            TargetPermanent(optional = true, count = 2, filter = TargetFilter.Land),
        )
        effect = Effects.Composite(
            Effects.DrawCards(3),
            Effects.UntapEachTarget(),
            Patterns.Hand.discardCards(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "28"
        artist = "Magali Villeneuve"
        flavorText =
            "\"I'm certain that the fate of Markov Manor is connected to these cryptoliths . . . " +
                "but with every page I turn, the less sure I am of the answer.\""
        imageUri =
            "https://cards.scryfall.io/normal/front/a/7/a7f9b8f0-f2b9-48ec-86c2-71d1419e396b.jpg?1783937850"
    }
}
