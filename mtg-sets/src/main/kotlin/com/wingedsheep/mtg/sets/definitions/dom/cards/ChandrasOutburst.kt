package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Chandra's Outburst
 * {3}{R}{R}
 * Sorcery
 * Chandra's Outburst deals 4 damage to target player or planeswalker.
 * Search your library and/or graveyard for a card named Chandra, Bold Pyromancer, reveal it,
 * and put it into your hand. If you search your library this way, shuffle.
 */
val ChandrasOutburst = card("Chandra's Outburst") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Chandra's Outburst deals 4 damage to target player or planeswalker.\n" +
        "Search your library and/or graveyard for a card named Chandra, Bold Pyromancer, reveal it, " +
        "and put it into your hand. If you search your library this way, shuffle."

    spell {
        val target = target("target player or planeswalker", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(4, target)
            .then(
                Patterns.Library.searchMultipleZones(
                    zones = listOf(Zone.LIBRARY, Zone.GRAVEYARD),
                    filter = GameObjectFilter.Any.named("Chandra, Bold Pyromancer"),
                    count = 1,
                    destination = SearchDestination.HAND,
                    reveal = true,
                )
            )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "276"
        artist = "Yongjae Choi"
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f1e849c3-f357-4e81-a580-be5056bed51b.jpg?1783934936"
    }
}
