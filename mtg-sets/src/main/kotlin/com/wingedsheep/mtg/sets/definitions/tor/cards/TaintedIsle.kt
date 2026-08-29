package com.wingedsheep.mtg.sets.definitions.tor.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
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
 * Tainted Isle
 * Land
 *
 * {T}: Add {C}.
 * {T}: Add {U} or {B}. Activate only if you control a Swamp.
 */
val TaintedIsle = card("Tainted Isle") {
    typeLine = "Land"
    colorIdentity = "UB"
    oracleText = "{T}: Add {C}.\n{T}: Add {U} or {B}. Activate only if you control a Swamp."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaOfChoiceEffect(ManaColorSet.Specific(setOf(Color.BLUE, Color.BLACK)))
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
        collectorNumber = "141"
        artist = "Alan Pollack"
        flavorText = "Only the foolish dare tread here."
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b462e121-015c-49c4-838a-ab788f213322.jpg?1783945139"
    }
}
