package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.GatherUntilMatchEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetOpponent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

private val opponentsCardsInExile: DynamicAmount =
    DynamicAmounts.zone(Player.EachOpponent, Zone.EXILE).count()

/**
 * Umbris, Fear Manifest
 * {3}{U}{B}
 * Legendary Creature — Nightmare Horror
 * 1/1
 *
 * Umbris gets +1/+1 for each card your opponents own in exile.
 * Whenever Umbris or another Nightmare or Horror you control enters, target opponent exiles
 * cards from the top of their library until they exile a land card.
 */
val UmbrisFearManifest = card("Umbris, Fear Manifest") {
    manaCost = "{3}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Nightmare Horror"
    power = 1
    toughness = 1
    oracleText = "Umbris gets +1/+1 for each card your opponents own in exile.\n" +
        "Whenever Umbris or another Nightmare or Horror you control enters, target opponent " +
        "exiles cards from the top of their library until they exile a land card."

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = opponentsCardsInExile,
            toughnessBonus = opponentsCardsInExile,
        )
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withAnySubtype(
                Subtype.NIGHTMARE.value,
                Subtype.HORROR.value,
            ).youControl(),
            binding = TriggerBinding.ANY,
        )
        target("target opponent", TargetOpponent())
        effect = Effects.Composite(
            GatherUntilMatchEffect(
                player = Player.TargetOpponent,
                filter = GameObjectFilter.Land,
                storeMatch = "land",
                storeRevealed = "revealed",
            ),
            MoveCollectionEffect(
                from = "revealed",
                destination = CardDestination.ToZone(Zone.EXILE),
            ),
        )
        description = "Whenever Umbris or another Nightmare or Horror you control enters, " +
            "target opponent exiles cards from the top of their library until they exile a land card."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "38"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7aead6a8-bada-42cf-b7cc-0b730f564582.jpg?1783924994"
    }
}
