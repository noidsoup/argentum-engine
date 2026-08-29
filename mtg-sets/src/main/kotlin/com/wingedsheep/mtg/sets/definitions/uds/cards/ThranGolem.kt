package com.wingedsheep.mtg.sets.definitions.uds.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.AttachmentKind
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Thran Golem
 * {5}
 * Artifact Creature — Golem
 * 3/3
 *
 * As long as this creature is enchanted, it gets +2/+2 and has flying, first strike, and trample.
 */
val ThranGolem = card("Thran Golem") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    oracleText = "As long as this creature is enchanted, it gets +2/+2 and has flying, first strike, and trample."
    power = 3
    toughness = 3

    val isEnchanted = Conditions.CompareAmounts(
        DynamicAmount.EntityProperty(
            EntityReference.Source,
            EntityNumericProperty.AttachmentCount(AttachmentKind.AURA),
        ),
        ComparisonOperator.GTE,
        DynamicAmount.Fixed(1),
    )

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantDynamicStatsEffect(
                filter = GroupFilter.source(),
                powerBonus = DynamicAmount.Fixed(2),
                toughnessBonus = DynamicAmount.Fixed(2),
            ),
            condition = isEnchanted,
        )
    }
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, GroupFilter.source()),
            condition = isEnchanted,
        )
    }
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FIRST_STRIKE, GroupFilter.source()),
            condition = isEnchanted,
        )
    }
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.TRAMPLE, GroupFilter.source()),
            condition = isEnchanted,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "141"
        artist = "Ron Spears"
        flavorText = "Karn felt more secure about his value to Urza when he realized he didn't need regular trimming."
        imageUri = "https://cards.scryfall.io/normal/front/5/7/5778c52b-248b-4131-b5c0-12ea1986786e.jpg?1783946053"
    }
}
