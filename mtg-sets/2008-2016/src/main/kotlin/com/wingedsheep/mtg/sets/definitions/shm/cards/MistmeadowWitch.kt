package com.wingedsheep.mtg.sets.definitions.shm.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Mistmeadow Witch
 * {1}{W/U}
 * Creature — Kithkin Wizard
 * 1/1
 *
 * {2}{W}{U}: Exile target creature. Return that card to the battlefield under its owner's control
 * at the beginning of the next end step.
 */
val MistmeadowWitch = card("Mistmeadow Witch") {
    manaCost = "{1}{W/U}"
    colorIdentity = "WU"
    typeLine = "Creature — Kithkin Wizard"
    power = 1
    toughness = 1
    oracleText = "{2}{W}{U}: Exile target creature. Return that card to the battlefield under its " +
        "owner's control at the beginning of the next end step."

    activatedAbility {
        cost = Costs.Mana("{2}{W}{U}")

        val creature = target(
            "target creature",
            TargetCreature(filter = TargetFilter.Creature),
        )

        effect = Effects.Composite(
            listOf(
                Effects.Move(creature, Zone.EXILE),
                CreateDelayedTriggerEffect(
                    step = Step.END,
                    effect = Effects.Move(creature, Zone.BATTLEFIELD),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "144"
        artist = "Greg Staples"
        flavorText = "Olka collected the evening mist for years, studying its secrets. Once she " +
            "learned its essence, she could vanish with a thought."
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e776fe0-6574-4fca-91bd-eb6e7383e5be.jpg?1783942737"
    }
}
