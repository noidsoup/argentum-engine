package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect

/**
 * Timber Gorge
 * Land
 * This land enters tapped.
 * {T}: Add {R} or {G}.
 *
 * Canonical printing: OGW (earliest real expansion). Later sets get Printing rows.
 */
val TimberGorge = card("Timber Gorge") {
    typeLine = "Land"
    colorIdentity = "RG"
    oracleText = "This land enters tapped.\n{T}: Add {R} or {G}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "179"
        artist = "Cliff Childs"
        flavorText = "\"Tazeem's embrace is harsh, but for those that call it home, nothing else will do.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55d05ff7-f071-4eb3-b10a-203741abdf10.jpg?1783937890"
    }
}
