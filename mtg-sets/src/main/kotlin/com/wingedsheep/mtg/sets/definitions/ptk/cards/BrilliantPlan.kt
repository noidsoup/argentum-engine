package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Brilliant Plan — Portal Three Kingdoms #36 (canonical; earliest real printing).
 * {4}{U} · Sorcery
 *
 * Draw three cards.
 *
 * Later scaffolded printings (C13, GS1, PZ2) contribute only [Printing] rows. ME3 is not
 * scaffolded yet, so no ME3 reprint row.
 */
val BrilliantPlan = card("Brilliant Plan") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Draw three cards."

    spell {
        effect = Effects.DrawCards(3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "36"
        artist = "Song Shikai"
        flavorText = "At Red Cliffs, Kongming and Zhou Yu each wrote his plan for defeating the Wei on the palm of his hand. They laughed as they both revealed the same word, \"Fire.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6acae374-9d71-4f8d-ba75-a983756624c7.jpg?1783946125"
    }
}
