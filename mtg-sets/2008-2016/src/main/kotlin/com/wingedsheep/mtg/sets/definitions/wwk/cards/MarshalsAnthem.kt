package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Marshal's Anthem
 * {2}{W}{W}
 * Enchantment
 *
 * Multikicker {1}{W}
 * Creatures you control get +1/+1.
 * When this enchantment enters, return up to X target creature cards from your graveyard to the
 * battlefield, where X is the number of times this enchantment was kicked.
 */
val MarshalsAnthem = card("Marshal's Anthem") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Multikicker {1}{W} (You may pay an additional {1}{W} any number of times as you cast this spell.)\n" +
        "Creatures you control get +1/+1.\n" +
        "When this enchantment enters, return up to X target creature cards from your graveyard to the battlefield, " +
        "where X is the number of times this enchantment was kicked."

    keywordAbility(KeywordAbility.multikicker("{1}{W}"))

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.youControl()),
        )
    }

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creatureCards = target(
            "creature cards from your graveyard",
            TargetObject(
                optional = true,
                filter = TargetFilter.CreatureInYourGraveyard,
                dynamicMaxCount = DynamicAmounts.kickerTimes(),
            ),
        )
        effect = ForEachTargetEffect(
            effects = listOf(
                Effects.Move(EffectTarget.ContextTarget(0), Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD),
            ),
        )
        description = "When this enchantment enters, return up to X target creature cards from your " +
            "graveyard to the battlefield, where X is the number of times this enchantment was kicked."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "15"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f38b1b7a-2f20-4a82-b143-7aeafc686dee.jpg?1783942066"
    }
}
