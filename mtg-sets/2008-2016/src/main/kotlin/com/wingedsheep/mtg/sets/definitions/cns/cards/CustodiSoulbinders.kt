package com.wingedsheep.mtg.sets.definitions.cns.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Custodi Soulbinders — Conspiracy (CNS) #17
 * {3}{W} · Creature — Human Cleric · 0/0
 *
 * This creature enters with X +1/+1 counters on it, where X is the number of other creatures on
 * the battlefield.
 * {2}{W}, Remove a +1/+1 counter from this creature: Create a 1/1 white Spirit creature token
 * with flying.
 *
 * Entry count matches [com.wingedsheep.mtg.sets.definitions.ons.cards.StagBeetle]: the entering
 * creature is not on the battlefield yet, so every creature already in play counts as "other".
 */
val CustodiSoulbinders = card("Custodi Soulbinders") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 0
    toughness = 0
    oracleText = "This creature enters with X +1/+1 counters on it, where X is the number of " +
        "other creatures on the battlefield.\n{2}{W}, Remove a +1/+1 counter from this creature: " +
        "Create a 1/1 white Spirit creature token with flying."

    replacementEffect(
        EntersWithDynamicCounters(
            count = DynamicAmount.AggregateBattlefield(Player.Each, GameObjectFilter.Creature),
        ),
    )

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{W}"),
            Costs.RemoveCounterFromSelf(Counters.PLUS_ONE_PLUS_ONE),
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            count = 1,
        )
        description = "{2}{W}, Remove a +1/+1 counter from this creature: Create a 1/1 white " +
            "Spirit creature token with flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "17"
        artist = "Karla Ortiz"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a21df7d2-d9c7-4b63-a275-ae7fe1630935.jpg?1783939378"
    }
}
