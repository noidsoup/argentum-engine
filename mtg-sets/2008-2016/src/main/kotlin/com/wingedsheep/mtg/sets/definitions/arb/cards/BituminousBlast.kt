package com.wingedsheep.mtg.sets.definitions.arb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bituminous Blast
 * {3}{B}{R}
 * Instant
 *
 * Cascade (When you cast this spell, exile cards from the top of your library until you exile a
 * nonland card that costs less. You may cast it without paying its mana cost. Put the exiled
 * cards on the bottom in a random order.)
 * Bituminous Blast deals 4 damage to target creature.
 *
 * Cascade is a cast trigger (CR 702.85a), so it is wired as [Triggers.WhenYouCastThisSpell] +
 * [Effects.Cascade] with [Keyword.CASCADE] for display — same shape as Bloodbraid Elf. Do not
 * ship keyword-only cascade.
 */
val BituminousBlast = card("Bituminous Blast") {
    manaCost = "{3}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Instant"
    oracleText = "Cascade (When you cast this spell, exile cards from the top of your library " +
        "until you exile a nonland card that costs less. You may cast it without paying its mana " +
        "cost. Put the exiled cards on the bottom in a random order.)\n" +
        "Bituminous Blast deals 4 damage to target creature."

    keywords(Keyword.CASCADE)

    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(4, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "34"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd6a3c48-b01e-4462-a4a1-8d5009a6a844.jpg?1783942435"

        ruling("2021-06-18", "A spell's mana value is determined only by its mana cost. Ignore any alternative costs, additional costs, cost increases, or cost reductions.")
        ruling("2021-06-18", "Cascade triggers when you cast the spell, meaning that it resolves before that spell. If you end up casting the exiled card, it will go on the stack above the spell with cascade.")
        ruling("2021-06-18", "When the cascade ability resolves, you must exile cards. The only optional part of the ability is whether or not you cast the last card exiled.")
        ruling("2021-06-18", "If a spell with cascade is countered, the cascade ability will still resolve normally.")
    }
}
