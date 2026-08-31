package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Journey for the Elixir — Global Series: Jiang Yanggu & Mu Yanling #36
 * {2}{G} · Sorcery
 *
 * Search your library and graveyard for a basic land card and a card named Jiang Yanggu, reveal
 * them, put them into your hand, then shuffle.
 */
val JourneyForTheElixir = card("Journey for the Elixir") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText =
        "Search your library and graveyard for a basic land card and a card named Jiang Yanggu, " +
            "reveal them, put them into your hand, then shuffle."

    spell {
        effect = Patterns.Library.searchMultipleZones(
            zones = listOf(Zone.LIBRARY, Zone.GRAVEYARD),
            filter = GameObjectFilter.BasicLand,
            count = 1,
            destination = SearchDestination.HAND,
            reveal = true,
        ).then(
            Patterns.Library.searchMultipleZones(
                zones = listOf(Zone.LIBRARY, Zone.GRAVEYARD),
                filter = GameObjectFilter.Any.named("Jiang Yanggu"),
                count = 1,
                destination = SearchDestination.HAND,
                reveal = true,
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "36"
        artist = "Qiu De En"
        flavorText =
            "\"As my hands touched it, I realized that I had not found it—it had been leading me here.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3da5b115-84b5-4eb3-bbef-e5024601ebc8.jpg?1783934623"
    }
}
