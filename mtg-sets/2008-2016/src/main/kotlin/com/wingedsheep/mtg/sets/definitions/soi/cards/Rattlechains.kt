package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantFlashToSpellType
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Rattlechains
 * {1}{U}
 * Creature — Spirit
 * 2/1
 *
 * Flash
 * Flying
 * When this creature enters, target Spirit gains hexproof until end of turn.
 * You may cast Spirit spells as though they had flash.
 *
 * "Target Spirit" is the bare tribal noun — any Spirit permanent, not only creatures or ones you
 * control ([BreathOfTheSleepless] uses the same [GrantFlashToSpellType] rail for the flash grant).
 */
val Rattlechains = card("Rattlechains") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 1
    oracleText = "Flash\n" +
        "Flying\n" +
        "When this creature enters, target Spirit gains hexproof until end of turn.\n" +
        "You may cast Spirit spells as though they had flash."

    keywords(Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val spirit = target(
            "target Spirit",
            TargetPermanent(
                filter = TargetFilter(GameObjectFilter.Permanent.withSubtype(Subtype.SPIRIT)),
            ),
        )
        effect = Effects.GrantKeyword(Keyword.HEXPROOF, spirit)
    }

    staticAbility {
        ability = GrantFlashToSpellType(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.SPIRIT),
            controllerOnly = true,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "81"
        artist = "Lius Lasahido"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6fc4db9-a29c-4f50-8e41-105b45af0be9.jpg?1783937791"
    }
}
