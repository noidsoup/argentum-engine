package com.wingedsheep.mtg.sets.definitions.bok.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Skullsnatcher
 * {1}{B}
 * Creature — Rat Ninja
 * 2/1
 *
 * Ninjutsu {B} ({B}, Return an unblocked attacker you control to hand: Put this card onto the
 * battlefield from your hand tapped and attacking.)
 * Whenever this creature deals combat damage to a player, exile up to two target cards from that
 * player's graveyard.
 *
 * Ninjutsu rides the engine's shared declare-blockers alternative-cost pipeline via the
 * [ninjutsu] helper — the cast is only offered after blockers are declared, charges {B}, returns
 * the chosen unblocked attacker to hand, and puts Skullsnatcher onto the battlefield tapped and
 * attacking the same defender (CR 506.3a).
 *
 * The damage trigger is targeted and scoped to the damaged player: `ownedByTriggeringPlayer()`
 * restricts the graveyard to the player Skullsnatcher just hit, and `count = 2, optional = true`
 * is the "up to two" shape — the ability can legally target zero, one, or two cards
 * (ruling 2005-02-01), so it stays on the stack and resolves even with nothing chosen.
 */
val Skullsnatcher = card("Skullsnatcher") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Rat Ninja"
    power = 2
    toughness = 1
    oracleText = "Ninjutsu {B} ({B}, Return an unblocked attacker you control to hand: Put this " +
        "card onto the battlefield from your hand tapped and attacking.)\n" +
        "Whenever this creature deals combat damage to a player, exile up to two target cards " +
        "from that player's graveyard."

    ninjutsu("{B}")

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        target(
            "up to two target cards from that player's graveyard",
            TargetObject(
                count = 2,
                optional = true,
                filter = TargetFilter(
                    GameObjectFilter.Any.ownedByTriggeringPlayer(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = ForEachTargetEffect(
            effects = listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.EXILE))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "84"
        artist = "Matt Cavotta"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6cb76fa1-a930-468d-bfd4-dd06c2a9fe9e.jpg?1783944195"

        ruling("2005-02-01", "Skullsnatcher's ability can target zero, one, or two cards in that player's graveyard.")
    }
}
