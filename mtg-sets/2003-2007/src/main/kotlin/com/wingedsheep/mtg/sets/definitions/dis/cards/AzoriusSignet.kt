package com.wingedsheep.mtg.sets.definitions.dis.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Azorius Signet — Dissension #159
 * {2} · Artifact
 * {1}, {T}: Add {W}{U}.
 */
val AzoriusSignet = card("Azorius Signet") {
    manaCost = "{2}"
    colorIdentity = "WU"
    typeLine = "Artifact"
    oracleText = "{1}, {T}: Add {W}{U}."
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.Composite(Effects.AddMana(Color.WHITE, 1), Effects.AddMana(Color.BLUE, 1))
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "159"
        artist = "Greg Hildebrandt"
        flavorText = "The Azorius signet is stamped on every permit issued by the Senate, making them a formality when one is already in hand."
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d91a527d-51f1-4fb1-9016-fc923fd43a6a.jpg?1783943383"
    }
}
