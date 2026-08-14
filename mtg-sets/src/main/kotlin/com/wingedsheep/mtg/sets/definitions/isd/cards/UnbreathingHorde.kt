package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PreventDamageAndRemoveCounter
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Unbreathing Horde
 * {2}{B}
 * Creature — Zombie
 * 0/0
 * This creature enters with a +1/+1 counter on it for each other Zombie you control and each
 * Zombie card in your graveyard.
 * If this creature would be dealt damage, prevent that damage and remove a +1/+1 counter from it.
 */
val UnbreathingHorde = card("Unbreathing Horde") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    oracleText =
        "This creature enters with a +1/+1 counter on it for each other Zombie you control and each " +
            "Zombie card in your graveyard.\n" +
            "If this creature would be dealt damage, prevent that damage and remove a +1/+1 counter from it."
    power = 0
    toughness = 0

    replacementEffect(
        EntersWithDynamicCounters(
            count = DynamicAmount.Add(
                DynamicAmount.AggregateBattlefield(
                    player = com.wingedsheep.sdk.scripting.references.Player.You,
                    filter = GameObjectFilter.Creature.withSubtype(Subtype.ZOMBIE),
                    excludeSelf = true,
                ),
                DynamicAmount.Count(
                    com.wingedsheep.sdk.scripting.references.Player.You,
                    Zone.GRAVEYARD,
                    GameObjectFilter.Creature.withSubtype(Subtype.ZOMBIE),
                ),
            ),
        ),
    )

    replacementEffect(
        PreventDamageAndRemoveCounter(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            appliesTo = EventPattern.DamageEvent(recipient = RecipientFilter.Self),
        ),
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "121"
        artist = "Dave Kendall"
        imageUri =
            "https://cards.scryfall.io/normal/front/1/a/1a91ea47-0c06-4333-a309-ac360c5cc9bd.jpg?1783940947"
    }
}
