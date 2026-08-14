package com.wingedsheep.mtg.sets.definitions.arb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodbraid Elf
 * {2}{R}{G}
 * Creature — Elf Berserker
 * 3/2
 *
 * Haste
 * Cascade
 *
 * Cascade is itself a "when you cast this spell" triggered ability (CR 702.85a), so it is wired as
 * an explicit cast trigger feeding the shared [Effects.Cascade] executor (which reads the triggering
 * spell's mana value to set the threshold). [Keyword.CASCADE] rides along for display and for
 * effects that care whether a spell has cascade.
 */
val BloodbraidElf = card("Bloodbraid Elf") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Elf Berserker"
    power = 3
    toughness = 2
    oracleText = "Haste (This creature can attack and {T} as soon as it comes under your control.)\n" +
        "Cascade (When you cast this spell, exile cards from the top of your library until you " +
        "exile a nonland card that costs less. You may cast it without paying its mana cost. Put " +
        "the exiled cards on the bottom in a random order.)"

    keywords(Keyword.HASTE, Keyword.CASCADE)

    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "50"
        artist = "Dominick Domingo"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7ef67487-c8e5-49bb-b0f7-e073ff2e31f1.jpg?1783942431"

        ruling("2021-06-18", "A spell's mana value is determined only by its mana cost. Ignore any alternative costs, additional costs, cost increases, or cost reductions.")
        ruling("2021-06-18", "Cascade triggers when you cast the spell, meaning that it resolves before that spell. If you end up casting the exiled card, it will go on the stack above the spell with cascade.")
        ruling("2021-06-18", "When the cascade ability resolves, you must exile cards. The only optional part of the ability is whether or not you cast the last card exiled.")
        ruling("2021-06-18", "If a spell with cascade is countered, the cascade ability will still resolve normally.")
        ruling("2021-06-18", "If the card has {X} in its mana cost, you must choose 0 as the value of X when casting it without paying its mana cost.")
        ruling("2021-06-18", "Due to a 2021 rules change to cascade, not only do you stop exiling cards if you exile a nonland card with lesser mana value than the spell with cascade, but the resulting spell you cast must also have lesser mana value.")
    }
}
