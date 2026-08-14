package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeywordToOwnSpells

/**
 * Firesong and Sunspeaker
 * {4}{R}{W}
 * Legendary Creature — Minotaur Cleric
 * 4/6
 *
 * Red instant and sorcery spells you control have lifelink.
 * Whenever a white instant or sorcery spell causes you to gain life, Firesong and Sunspeaker
 * deals 3 damage to target creature or player.
 *
 * Second ability requires F-LIFEGAIN-CAUSE ([Triggers.spellCausesYouToGainLife]).
 */
val FiresongAndSunspeaker = card("Firesong and Sunspeaker") {
    manaCost = "{4}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Minotaur Cleric"
    power = 4
    toughness = 6
    oracleText = "Red instant and sorcery spells you control have lifelink.\n" +
        "Whenever a white instant or sorcery spell causes you to gain life, " +
        "Firesong and Sunspeaker deals 3 damage to target creature or player."

    staticAbility {
        ability = GrantKeywordToOwnSpells(
            keyword = Keyword.LIFELINK,
            spellFilter = GameObjectFilter.InstantOrSorcery.withColor(Color.RED),
        )
    }

    triggeredAbility {
        trigger = Triggers.spellCausesYouToGainLife(
            GameObjectFilter.InstantOrSorcery.withColor(Color.WHITE)
        )
        val t = target("target creature or player", Targets.CreatureOrPlayer)
        effect = Effects.DealDamage(3, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "280"
        artist = "Zoltan Boros"
        flavorText = "The peaks of Hurloon never fall silent."
        imageUri = "https://cards.scryfall.io/normal/front/3/9/3923708a-60b8-4fb0-beb2-9ab18f5fd381.jpg?1783934934"
        ruling(
            "2018-04-27",
            "A spell causes you to gain life if its cost or effect instructs you to gain life or " +
                "if an instruction in its cost or effect is modified by a replacement effect and " +
                "the modified event includes you gaining life. If a spell's cost or effect " +
                "instructs a source with lifelink you control to deal damage, that spell causes " +
                "that life gain as well."
        )
        ruling(
            "2018-04-27",
            "Firesong and Sunspeaker's last ability doesn't trigger if a triggered ability of a " +
                "white instant or sorcery spell or card causes you to gain life, such as the " +
                "triggered ability of Renewed Faith when it's cycled."
        )
        ruling(
            "2023-04-14",
            "The last ability of Firesong and Sunspeaker can't target a planeswalker or battle."
        )
    }
}
