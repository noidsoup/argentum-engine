package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Nest Invader
 * {1}{G}
 * Creature — Eldrazi Drone
 * 2/2
 *
 * When this creature enters, create a 0/1 colorless Eldrazi Spawn creature token. It has
 * "Sacrifice this token: Add {C}."
 */
val NestInvader = card("Nest Invader") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Eldrazi Drone"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, create a 0/1 colorless Eldrazi Spawn creature token. " +
        "It has \"Sacrifice this token: Add {C}.\""

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            power = 0,
            toughness = 1,
            colors = emptySet(),
            creatureTypes = setOf("Eldrazi", "Spawn"),
            activatedAbilities = listOf(
                ActivatedAbility(
                    id = AbilityId.generate(),
                    cost = Costs.SacrificeSelf,
                    effect = Effects.AddColorlessMana(1),
                    isManaAbility = true,
                    timing = TimingRule.ManaAbility
                )
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "201"
        artist = "Trevor Claxton"
        flavorText = "It nurtures its masters' glorious future."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24517d9c-6cde-41e8-9e82-ee73f069379a.jpg?1783941961"
    }
}
