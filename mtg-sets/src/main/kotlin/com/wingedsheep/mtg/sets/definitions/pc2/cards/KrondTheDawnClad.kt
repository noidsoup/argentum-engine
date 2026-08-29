package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.values.AttachmentKind
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Krond the Dawn-Clad
 * {G}{G}{G}{W}{W}{W}
 * Legendary Creature — Archon
 * 6/6
 *
 * Flying, vigilance
 * Whenever Krond attacks, if it's enchanted, exile target permanent.
 */
val KrondTheDawnClad = card("Krond the Dawn-Clad") {
    manaCost = "{G}{G}{G}{W}{W}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Archon"
    oracleText = "Flying, vigilance\nWhenever Krond attacks, if it's enchanted, exile target permanent."
    power = 6
    toughness = 6

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    val isEnchanted = Conditions.CompareAmounts(
        DynamicAmount.EntityProperty(
            EntityReference.Source,
            EntityNumericProperty.AttachmentCount(AttachmentKind.AURA),
        ),
        ComparisonOperator.GTE,
        DynamicAmount.Fixed(1),
    )

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = isEnchanted
        val target = target("permanent", Targets.Permanent)
        effect = Effects.Exile(target)
        description = "Whenever Krond attacks, if it's enchanted, exile target permanent."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "99"
        artist = "Zoltan Boros"
        flavorText = "Krond, the personification of the dawn's light, lives to exact justice on his nemesis Vela."
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63d25986-2d71-43b8-b660-502acef9a70c.jpg?1783940595"
    }
}
