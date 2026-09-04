package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Whirler Rogue — Magic Origins (ORI) #83
 * {2}{U}{U} · Creature — Human Rogue Artificer · 2/2
 *
 * When this creature enters, create two 1/1 colorless Thopter artifact creature tokens with flying.
 * Tap two untapped artifacts you control: Target creature can't be blocked this turn.
 */
val WhirlerRogue = card("Whirler Rogue") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Rogue Artificer"
    oracleText = "When this creature enters, create two 1/1 colorless Thopter artifact creature " +
        "tokens with flying.\n" +
        "Tap two untapped artifacts you control: Target creature can't be blocked this turn."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            creatureTypes = setOf("Thopter"),
            keywords = setOf(Keyword.FLYING),
            count = 2,
            artifactToken = true,
        )
    }

    activatedAbility {
        cost = Costs.TapPermanents(
            count = 2,
            filter = GameObjectFilter.Artifact,
        )
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "83"
        artist = "Winona Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0feb66d2-c50f-4296-af2f-b374c57443b0.jpg?1783938345"
    }
}
