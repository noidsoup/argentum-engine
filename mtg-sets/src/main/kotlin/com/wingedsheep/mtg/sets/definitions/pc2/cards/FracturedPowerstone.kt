package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Fractured Powerstone
 * {2}
 * Artifact
 *
 * {T}: Add {C}.
 * {T}: Roll the planar die. Activate only as a sorcery.
 */
val FracturedPowerstone = card("Fractured Powerstone") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{T}: Add {C}.\n{T}: Roll the planar die. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.RollPlanarDie
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "111"
        artist = "Jason Felix"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b18bdf58-99b7-4307-b6a5-186ff1594fbb.jpg?1783940617"
    }
}
