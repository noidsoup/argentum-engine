package com.wingedsheep.mtg.sets.definitions.c13.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.LoseAllAbilities
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.TransformPermanent

/**
 * Darksteel Mutation — Commander 2013 (C13) #9
 * {1}{W} · Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature is an Insect artifact creature with base power and toughness 0/1 and has
 * indestructible, and it loses all other abilities, card types, and creature types.
 *
 * Modeled as a stack of statics on the enchanted creature, mirroring Witness Protection /
 * Sugar Coat's "becomes a different thing entirely" shape:
 *  - [TransformPermanent] Layer 4 replaces all card types with Artifact and Creature and all
 *    creature subtypes with Insect.
 *  - [SetBasePowerToughnessStatic] 0/1 — Layer 7b.
 *  - [LoseAllAbilities] — Layer 6.
 *  - [GrantKeyword] indestructible — Layer 6, granted by the Aura after stripping the host's
 *    abilities.
 */
val DarksteelMutation = card("Darksteel Mutation") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature is an Insect artifact creature with base " +
        "power and toughness 0/1 and has indestructible, and it loses all other abilities, card " +
        "types, and creature types."

    auraTarget = Targets.Creature

    staticAbility {
        ability = TransformPermanent(
            setCardTypes = setOf("ARTIFACT", "CREATURE"),
            setSubtypes = setOf(Subtype.INSECT.value),
        )
    }

    staticAbility {
        ability = SetBasePowerToughnessStatic(0, 1)
    }

    staticAbility {
        ability = LoseAllAbilities()
    }

    staticAbility {
        ability = GrantKeyword(Keyword.INDESTRUCTIBLE)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "9"
        artist = "Daniel Ljunggren"
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df7d800b-0120-4036-81d7-dec60ccc8057.jpg?1783939691"
    }
}
