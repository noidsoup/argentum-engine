package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Ghostly Flicker
 * {2}{U}
 * Instant
 *
 * Exile two target artifacts, creatures, and/or lands you control, then return those cards to the
 * battlefield under your control.
 *
 * The blink is one atomic gather → exile (linked to this spell) → return-from-linked-exile
 * pipeline (Lake-town Mariners / Gone Fishing), seeded from [CardSource.ChosenTargets] because the
 * two permanents are targets locked in at cast time. Both leave and both re-enter as one batch so
 * ETB abilities see the pair together. Default `underOwnersControl = false` on the return move
 * matches "under your control" (the caster), not "under their owner's control".
 */
val GhostlyFlicker = card("Ghostly Flicker") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Exile two target artifacts, creatures, and/or lands you control, then return " +
        "those cards to the battlefield under your control."

    spell {
        target(
            "two target artifacts, creatures, and/or lands you control",
            TargetPermanent(
                count = 2,
                filter = TargetFilter(
                    GameObjectFilter.Artifact or GameObjectFilter.Creature or GameObjectFilter.Land
                ).youControl()
            )
        )
        effect = Effects.Pipeline(
            descriptionOverride = "Exile two target artifacts, creatures, and/or lands you control, " +
                "then return those cards to the battlefield under your control"
        ) {
            val chosen = gather(source = CardSource.ChosenTargets)
            exile(chosen, linkToSource = true)
            val returning = gather(source = CardSource.FromLinkedExile())
            move(returning, CardDestination.ToZone(Zone.BATTLEFIELD))
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0a44373-0c50-4e14-a7c6-0de66796b81e.jpg?1783940717"
    }
}
