package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Maelstrom Wanderer
 * {5}{G}{U}{R}
 * Legendary Creature — Elemental
 * 7/5
 *
 * Creatures you control have haste.
 * Cascade, cascade
 *
 * Two separate cascade instances each trigger on cast (CR 702.85a / multiple instances each
 * trigger separately). [Keyword.CASCADE] alone is display-only — wire explicit cast triggers
 * feeding [Effects.Cascade], matching Bloodbraid Elf.
 */
val MaelstromWanderer = card("Maelstrom Wanderer") {
    manaCost = "{5}{G}{U}{R}"
    colorIdentity = "URG"
    typeLine = "Legendary Creature — Elemental"
    oracleText = "Creatures you control have haste.\n" +
        "Cascade, cascade (When you cast this spell, exile cards from the top of your library " +
        "until you exile a nonland card that costs less. You may cast it without paying its mana " +
        "cost. Put the exiled cards on the bottom in a random order. Then do it again.)"
    power = 7
    toughness = 5
    keywords(Keyword.CASCADE)

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, GroupFilter(GameObjectFilter.Creature.youControl()))
    }

    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }
    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "101"
        artist = "Thomas M. Baxa"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/9129baf5-ffa9-4ffb-bcab-19d6a42dbfcc.jpg"
    }
}
