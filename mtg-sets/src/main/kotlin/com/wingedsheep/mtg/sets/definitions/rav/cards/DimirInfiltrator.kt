package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Dimir Infiltrator
 * {U}{B}
 * Creature — Spirit
 * 1/3
 *
 * This creature can't be blocked.
 * Transmute {1}{U}{B} ({1}{U}{B}, Discard this card: Search your library for a card with the same
 * mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)
 */
val DimirInfiltrator = card("Dimir Infiltrator") {
    manaCost = "{U}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 3
    oracleText =
        "This creature can't be blocked.\n" +
            "Transmute {1}{U}{B} ({1}{U}{B}, Discard this card: Search your library for a card with the " +
            "same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only " +
            "as a sorcery.)"

    flags(AbilityFlag.CANT_BE_BLOCKED)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}{B}"), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        timing = TimingRule.SorcerySpeed
        effect = Patterns.Mechanic.transmuteSearch()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "203"
        artist = "Jim Nelson"
        flavorText = "The Dimir call it the silent circle. It has always been and will always be, and is ours."
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3db9204c-dde8-4241-aac2-1f090566f604.jpg?1783943709"
    }
}
