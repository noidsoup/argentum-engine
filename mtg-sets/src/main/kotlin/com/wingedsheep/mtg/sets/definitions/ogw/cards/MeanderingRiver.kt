package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Meandering River
 * Land
 * This land enters tapped.
 * {T}: Add {W} or {U}.
 *
 * Canonical printing: OGW (earliest real expansion). Later sets get Printing rows.
 */
val MeanderingRiver = card("Meandering River") {
    typeLine = "Land"
    colorIdentity = "WU"
    oracleText = "This land enters tapped.\n{T}: Add {W} or {U}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "173"
        artist = "Cliff Childs"
        flavorText = "The river split into many channels as it flowed to the Halimar Sea."
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a5bedf1-92b6-465c-afc2-ce8e150a5e57.jpg?1783937892"
    }
}
