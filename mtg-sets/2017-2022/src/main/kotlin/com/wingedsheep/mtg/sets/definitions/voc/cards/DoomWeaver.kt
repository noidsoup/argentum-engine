package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.soulbond
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Doom Weaver
 * {4}{B}{B}
 * Creature — Spider Horror
 * 1/8
 *
 * Reach
 * Soulbond (You may pair this creature with another unpaired creature when either enters. They
 * remain paired for as long as you control both of them.)
 * As long as Doom Weaver is paired with another creature, each of those creatures has "When this
 * creature dies, draw cards equal to its power."
 *
 * [GrantTriggeredAbility] over [GroupFilter.soulbondPair] — the same shape as [ImperiousMindbreaker]
 * and [BreathkeeperSeraph]. [TriggerBinding.SELF] makes "this creature" mean whichever paired half
 * died, and [DynamicAmounts.triggeringPower] reads last-known power at resolution.
 */
val DoomWeaver = card("Doom Weaver") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spider Horror"
    oracleText =
        "Reach\n" +
            "Soulbond (You may pair this creature with another unpaired creature when either enters. " +
            "They remain paired for as long as you control both of them.)\n" +
            "As long as Doom Weaver is paired with another creature, each of those creatures has " +
            "\"When this creature dies, draw cards equal to its power.\""
    power = 1
    toughness = 8

    keywords(Keyword.REACH)
    soulbond()

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.Dies.event,
                binding = TriggerBinding.SELF,
                effect = Effects.DrawCards(DynamicAmounts.triggeringPower()),
                descriptionOverride = "When this creature dies, draw cards equal to its power.",
            ),
            filter = GroupFilter.soulbondPair(),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "34"
        artist = "Helge C. Balzer"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08c79a77-810d-4ee2-8afc-18ddfba41ad1.jpg?1783924996"
    }
}
