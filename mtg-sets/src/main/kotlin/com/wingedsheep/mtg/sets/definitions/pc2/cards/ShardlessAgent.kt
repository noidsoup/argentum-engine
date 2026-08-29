package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shardless Agent
 * {1}{G}{U}
 * Artifact Creature — Human Rogue
 * 2/2
 *
 * Cascade
 *
 * Planechase 2012 is this card's earliest real printing. Cascade is a cast trigger (CR 702.85a), wired
 * as [Triggers.WhenYouCastThisSpell] + [Effects.Cascade] with [Keyword.CASCADE] for display — same
 * shape as Bloodbraid Elf / Bituminous Blast.
 */
val ShardlessAgent = card("Shardless Agent") {
    manaCost = "{1}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Artifact Creature — Human Rogue"
    power = 2
    toughness = 2
    oracleText = "Cascade (When you cast this spell, exile cards from the top of your library until " +
        "you exile a nonland card that costs less. You may cast it without paying its mana cost. Put " +
        "the exiled cards on the bottom in a random order.)"

    keywords(Keyword.CASCADE)

    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "104"
        artist = "Izzy"
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ceb4df89-a97e-4479-b7f0-7083417a9565.jpg?1783940595"

        ruling("2021-06-18", "A spell's mana value is determined only by its mana cost. Ignore any alternative costs, additional costs, cost increases, or cost reductions.")
        ruling("2021-06-18", "Cascade triggers when you cast the spell, meaning that it resolves before that spell. If you end up casting the exiled card, it will go on the stack above the spell with cascade.")
        ruling("2021-06-18", "When the cascade ability resolves, you must exile cards. The only optional part of the ability is whether or not you cast the last card exiled.")
        ruling("2021-06-18", "If a spell with cascade is countered, the cascade ability will still resolve normally.")
        ruling("2021-06-18", "If the card has {X} in its mana cost, you must choose 0 as the value of X when casting it without paying its mana cost.")
        ruling("2021-06-18", "Due to a 2021 rules change to cascade, not only do you stop exiling cards if you exile a nonland card with lesser mana value than the spell with cascade, but the resulting spell you cast must also have lesser mana value.")
    }
}
