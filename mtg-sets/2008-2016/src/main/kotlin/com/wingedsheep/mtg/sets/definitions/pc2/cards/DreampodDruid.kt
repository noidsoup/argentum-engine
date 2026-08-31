package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.values.AttachmentKind
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Dreampod Druid
 * {1}{G}
 * Creature — Human Druid
 * 2/2
 *
 * At the beginning of each upkeep, if this creature is enchanted, create a 1/1 green Saproling creature token.
 */
val DreampodDruid = card("Dreampod Druid") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    oracleText = "At the beginning of each upkeep, if this creature is enchanted, create a 1/1 green Saproling creature token."
    power = 2
    toughness = 2

    val isEnchanted = Conditions.CompareAmounts(
        DynamicAmount.EntityProperty(
            EntityReference.Source,
            EntityNumericProperty.AttachmentCount(AttachmentKind.AURA),
        ),
        ComparisonOperator.GTE,
        DynamicAmount.Fixed(1),
    )

    triggeredAbility {
        trigger = Triggers.EachUpkeep
        interveningIf = isEnchanted
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling"),
            imageUri = "https://cards.scryfall.io/normal/front/2/4/248ade83-ac57-42d6-985c-1e4cc3639f36.jpg?1783903570",
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "64"
        artist = "Wayne Reynolds"
        flavorText = "\"Don't mistake my creations for mere vegetation. They are my children, loyal and fierce.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/9/2908c551-6798-4f05-826b-714b4ef131f7.jpg?1783940610"
    }
}
