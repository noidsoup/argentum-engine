package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Rhythmic Water Vortex — Global Series: Jiang Yanggu & Mu Yanling #18
 * {3}{U}{U} · Sorcery
 *
 * Return up to two target creatures to their owners' hands.
 * Search your library and/or graveyard for a card named Mu Yanling, reveal it, and put it into
 * your hand. If you searched your library this way, shuffle.
 */
val RhythmicWaterVortex = card("Rhythmic Water Vortex") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText =
        "Return up to two target creatures to their owner's hand.\n" +
            "Search your library and/or graveyard for a card named Mu Yanling, reveal it, and put it " +
            "into your hand. If you searched your library this way, shuffle."

    spell {
        val t = target(
            "up to two target creatures",
            TargetCreature(optional = true, count = 2, filter = TargetFilter.Creature),
        )
        effect = ForEachTargetEffect(listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.HAND)))
            .then(
                Patterns.Library.searchMultipleZones(
                    zones = listOf(Zone.LIBRARY, Zone.GRAVEYARD),
                    filter = GameObjectFilter.Any.named("Mu Yanling"),
                    count = 1,
                    destination = SearchDestination.HAND,
                    reveal = true,
                ),
            )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "18"
        artist = "Xin-Yu Liu"
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be82ea29-66d4-471d-bef7-c6524207b96d.jpg?1783934631"
    }
}
