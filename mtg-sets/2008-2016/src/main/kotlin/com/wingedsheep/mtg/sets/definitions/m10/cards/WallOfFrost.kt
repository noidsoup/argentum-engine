package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wall of Frost
 * {1}{U}{U}
 * Creature — Wall
 * 0/7
 *
 * Defender
 * Whenever this creature blocks a creature, that creature doesn't untap during its controller's
 * next untap step.
 */
val WallOfFrost = card("Wall of Frost") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Wall"
    power = 0
    toughness = 7
    oracleText = "Defender\n" +
        "Whenever this creature blocks a creature, that creature doesn't untap during its " +
        "controller's next untap step."

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.blocks(attackerFilter = GameObjectFilter.Creature)
        effect = GrantKeywordEffect(
            AbilityFlag.DOESNT_UNTAP.name,
            EffectTarget.TriggeringEntity,
            Duration.UntilAfterAffectedControllersNextUntap,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "80"
        artist = "Mike Bierek"
        flavorText = "\"I welcome it. The pasture is cleared of weeds and the wolves are frozen solid.\"\n—Lumi, goatherd"
        imageUri = "https://cards.scryfall.io/normal/front/1/7/17bc35a7-e38b-4c15-889a-d58c8b360315.jpg?1783942386"
    }
}
