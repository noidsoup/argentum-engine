package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.soulbond
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Imperious Mindbreaker
 * {1}{U}{U}
 * Creature — Human Wizard
 * 1/4
 *
 * Soulbond (You may pair this creature with another unpaired creature when either enters. They
 * remain paired for as long as you control both of them.)
 * As long as Imperious Mindbreaker is paired with another creature, each of those creatures has
 * "Whenever this creature attacks, each opponent mills cards equal to its toughness."
 *
 * [GrantTriggeredAbility] over [GroupFilter.soulbondPair] — the same shape as [ThunderingMightmare]
 * and [TandemLookout]. [TriggerBinding.SELF] makes "this creature" mean whichever paired half
 * attacked, and [DynamicAmounts.sourceToughness] reads that creature's toughness at resolution.
 */
val ImperiousMindbreaker = card("Imperious Mindbreaker") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText =
        "Soulbond (You may pair this creature with another unpaired creature when either enters. " +
            "They remain paired for as long as you control both of them.)\n" +
            "As long as Imperious Mindbreaker is paired with another creature, each of those creatures has " +
            "\"Whenever this creature attacks, each opponent mills cards equal to its toughness.\""
    power = 1
    toughness = 4

    soulbond()

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.Attacks.event,
                binding = TriggerBinding.SELF,
                effect = Patterns.Library.mill(
                    DynamicAmounts.sourceToughness(),
                    EffectTarget.PlayerRef(Player.EachOpponent),
                ),
                descriptionOverride =
                    "Whenever this creature attacks, each opponent mills cards equal to its toughness.",
            ),
            filter = GroupFilter.soulbondPair(),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "33"
        artist = "Olivier Bernard"
        imageUri = "https://cards.scryfall.io/normal/front/9/5/95a9e722-d7a3-4a19-afd6-6c617aaf8eda.jpg?1783924996"
    }
}
