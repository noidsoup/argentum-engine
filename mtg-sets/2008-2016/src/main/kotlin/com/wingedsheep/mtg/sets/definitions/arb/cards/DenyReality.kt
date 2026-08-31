package com.wingedsheep.mtg.sets.definitions.arb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Deny Reality
 * {3}{U}{B}
 * Sorcery
 *
 * Cascade (When you cast this spell, exile cards from the top of your library until you exile a
 * nonland card that costs less. You may cast it without paying its mana cost. Put the exiled
 * cards on the bottom in a random order.)
 * Return target permanent to its owner's hand.
 *
 * Cascade is a cast trigger (CR 702.85a), wired as [Triggers.WhenYouCastThisSpell] +
 * [Effects.Cascade] with [Keyword.CASCADE] for display — same shape as Bituminous Blast.
 */
val DenyReality = card("Deny Reality") {
    manaCost = "{3}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Sorcery"
    oracleText = "Cascade (When you cast this spell, exile cards from the top of your library " +
        "until you exile a nonland card that costs less. You may cast it without paying its mana " +
        "cost. Put the exiled cards on the bottom in a random order.)\n" +
        "Return target permanent to its owner's hand."

    keywords(Keyword.CASCADE)

    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }

    spell {
        val permanent = target("target permanent", Targets.Permanent)
        effect = Effects.ReturnToHand(permanent)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Jean-Sébastien Rossbach"
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f138fe9f-3cb9-4f16-adc4-76fcb0c7e064.jpg?1783942438"

        ruling("2021-06-18", "A spell's mana value is determined only by its mana cost. Ignore any alternative costs, additional costs, cost increases, or cost reductions.")
        ruling("2021-06-18", "Cascade triggers when you cast the spell, meaning that it resolves before that spell. If you end up casting the exiled card, it will go on the stack above the spell with cascade.")
        ruling("2021-06-18", "When the cascade ability resolves, you must exile cards. The only optional part of the ability is whether or not you cast the last card exiled.")
        ruling("2021-06-18", "If a spell with cascade is countered, the cascade ability will still resolve normally.")
    }
}
