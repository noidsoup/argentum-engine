package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantFlashToSpellType
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Breath of the Sleepless
 * {3}{U}
 * Enchantment
 *
 * You may cast Spirit spells as though they had flash.
 * Whenever you cast a creature spell during an opponent's turn, tap up to one target creature.
 */
val BreathOfTheSleepless = card("Breath of the Sleepless") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "You may cast Spirit spells as though they had flash.\n" +
        "Whenever you cast a creature spell during an opponent's turn, tap up to one target creature."

    staticAbility {
        ability = GrantFlashToSpellType(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.SPIRIT),
            controllerOnly = true,
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastCreature
        triggerRestriction = Conditions.IsNotYourTurn
        val creature = target(
            "target",
            TargetCreature(optional = true),
        )
        effect = Effects.Tap(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "11"
        artist = "Robin Olausson"
        flavorText = "Worse than the obscuring mists are the ghastly faces that appear in the " +
            "glow of spectral lanterns."
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c59af37-5c01-4f65-97f6-2478aa29949a.jpg?1783925004"
    }
}
