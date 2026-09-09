package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ghostly Pilferer
 * {1}{U}
 * Creature — Spirit Rogue
 * 2/1
 *
 * Whenever this creature becomes untapped, you may pay {2}. If you do, draw a card.
 * Whenever an opponent casts a spell from anywhere other than their hand, draw a card.
 * Discard a card: This creature can't be blocked this turn.
 *
 * The non-hand cast trigger uses [SpellCastPredicate.CastFromZoneOtherThan] on
 * [Triggers.opponentCasts] — flashback, foretell, adventure, and other zone casts count; a normal
 * hand cast does not. The untap payoff is [MayPayManaEffect]; unblockable is
 * [Effects.GrantKeyword] over [AbilityFlag.CANT_BE_BLOCKED] on a [Costs.DiscardCard] activation.
 */
val GhostlyPilferer = card("Ghostly Pilferer") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit Rogue"
    oracleText =
        "Whenever this creature becomes untapped, you may pay {2}. If you do, draw a card.\n" +
            "Whenever an opponent casts a spell from anywhere other than their hand, draw a card.\n" +
            "Discard a card: This creature can't be blocked this turn."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.BecomesUntapped
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{2}"),
            effect = Effects.DrawCards(1),
        )
        description = "Whenever this creature becomes untapped, you may pay {2}. If you do, draw a card."
    }

    triggeredAbility {
        trigger = Triggers.opponentCasts(
            requires = setOf(SpellCastPredicate.CastFromZoneOtherThan(Zone.HAND)),
        )
        effect = Effects.DrawCards(1)
        description =
            "Whenever an opponent casts a spell from anywhere other than their hand, draw a card."
    }

    activatedAbility {
        cost = Costs.DiscardCard
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, EffectTarget.Self)
        description = "Discard a card: This creature can't be blocked this turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "52"
        artist = "Craig J Spearing"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/2810631f-c55c-4947-a26f-4d3ce76024b3.jpg?1783930726"
    }
}
