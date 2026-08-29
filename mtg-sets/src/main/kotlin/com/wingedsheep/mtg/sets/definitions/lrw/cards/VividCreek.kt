package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Vivid Creek
 * Land
 *
 * This land enters tapped with two charge counters on it.
 * {T}: Add {U}.
 * {T}, Remove a charge counter from this land: Add one mana of any color.
 */
val VividCreek = card("Vivid Creek") {
    typeLine = "Land"
    colorIdentity = "U"
    oracleText = "This land enters tapped with two charge counters on it.\n" +
        "{T}: Add {U}.\n" +
        "{T}, Remove a charge counter from this land: Add one mana of any color."

    replacementEffect(EntersTapped())
    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.CHARGE),
            count = 2,
            selfOnly = true,
        )
    )

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.RemoveCounterFromSelf(Counters.CHARGE, 1))
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "276"
        artist = "Fred Fields"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e3c2935-84ee-446d-b247-2f574ea84a8f.jpg?1783942846"
    }
}
