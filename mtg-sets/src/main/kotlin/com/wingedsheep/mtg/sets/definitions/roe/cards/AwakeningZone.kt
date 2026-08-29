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
 * Awakening Zone
 * {2}{G}
 * Enchantment
 * At the beginning of your upkeep, you may create a 0/1 colorless Eldrazi Spawn creature token.
 * It has "Sacrifice this token: Add {C}."
 */
val AwakeningZone = card("Awakening Zone") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText =
        "At the beginning of your upkeep, you may create a 0/1 colorless Eldrazi Spawn creature " +
            "token. It has \"Sacrifice this token: Add {C}.\""
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        optional = true
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
                    timing = TimingRule.ManaAbility,
                ),
            ),
        )
    }
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "176"
        artist = "Johann Bodin"
        flavorText = "The ground erupted in time with the hedron's thrum, a dirge of the last days."
        imageUri = "https://cards.scryfall.io/normal/front/0/8/080dbd69-95a8-4fed-bbaf-875a8a34a2c9.jpg"
    }
}
