package com.wingedsheep.mtg.sets.definitions.tor.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AddManaOfChoiceEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.ManaColorSet

/**
 * Tainted Peak
 * Land
 *
 * {T}: Add {C}.
 * {T}: Add {B} or {R}. Activate only if you control a Swamp.
 */
val TaintedPeak = card("Tainted Peak") {
    typeLine = "Land"
    colorIdentity = "BR"
    oracleText = "{T}: Add {C}.\n{T}: Add {B} or {R}. Activate only if you control a Swamp."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaOfChoiceEffect(ManaColorSet.Specific(setOf(Color.BLACK, Color.RED)))
        manaAbility = true
        timing = TimingRule.ManaAbility
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Swamp")),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "142"
        artist = "Tony Szczudlo"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4dcaaabe-e1d7-4047-9960-79178af3d903.jpg?1783945139"
    }
}
