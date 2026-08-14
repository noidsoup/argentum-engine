package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Etherium-Horn Sorcerer
 * {4}{U}{R}
 * Artifact Creature — Minotaur Wizard Sorcerer
 * 3/6
 *
 * {1}{U}{R}: Return this creature to its owner's hand.
 * Cascade
 *
 * Planechase 2012 is this card's earliest printing, so the canonical definition lives here rather
 * than in a reprinting set.
 *
 * The self-bounce is an ordinary activated ability that functions only from the battlefield, so if
 * this creature has already left by the time the ability resolves, the [EffectTarget.Self] move is
 * a no-op (ruling 2012-06-01) — the card stays wherever it went.
 *
 * Cascade is itself a "when you cast this spell" triggered ability (CR 702.85a), so it is wired as
 * an explicit cast trigger feeding the shared [Effects.Cascade] executor (which reads the triggering
 * spell's mana value to set the threshold). [Keyword.CASCADE] rides along for display and for
 * effects that care whether a spell has cascade.
 */
val EtheriumHornSorcerer = card("Etherium-Horn Sorcerer") {
    manaCost = "{4}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Artifact Creature — Minotaur Wizard Sorcerer"
    power = 3
    toughness = 6
    oracleText = "{1}{U}{R}: Return this creature to its owner's hand.\n" +
        "Cascade (When you cast this spell, exile cards from the top of your library until you " +
        "exile a nonland card that costs less. You may cast it without paying its mana cost. Put " +
        "the exiled cards on the bottom in a random order.)"

    keywords(Keyword.CASCADE)

    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }

    activatedAbility {
        cost = Costs.Mana("{1}{U}{R}")
        effect = Effects.Move(EffectTarget.Self, Zone.HAND)
        description = "{1}{U}{R}: Return this creature to its owner's hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "91"
        artist = "Franz Vohwinkel"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3df330d3-7687-4a05-b215-adca15f332da.jpg?1783940599"

        ruling("2012-06-01", "Etherium-Horn Sorcerer's first ability can only be activated when it is on the battlefield. If Etherium-Horn Sorcerer isn't on the battlefield when the ability resolves, the ability won't do anything.")
        ruling("2021-06-18", "A spell's mana value is determined only by its mana cost. Ignore any alternative costs, additional costs, cost increases, or cost reductions.")
        ruling("2021-06-18", "Cascade triggers when you cast the spell, meaning that it resolves before that spell. If you end up casting the exiled card, it will go on the stack above the spell with cascade.")
        ruling("2021-06-18", "When the cascade ability resolves, you must exile cards. The only optional part of the ability is whether or not you cast the last card exiled.")
        ruling("2021-06-18", "If a spell with cascade is countered, the cascade ability will still resolve normally.")
    }
}
