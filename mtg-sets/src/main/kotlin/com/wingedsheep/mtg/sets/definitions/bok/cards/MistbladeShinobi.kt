package com.wingedsheep.mtg.sets.definitions.bok.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Mistblade Shinobi
 * {2}{U}
 * Creature — Human Ninja
 * 1/1
 *
 * Ninjutsu {U} ({U}, Return an unblocked attacker you control to hand: Put this card onto the
 * battlefield from your hand tapped and attacking.)
 * Whenever this creature deals combat damage to a player, you may return target creature that
 * player controls to its owner's hand.
 *
 * Ninjutsu rides the engine's shared declare-blockers alternative-cost pipeline via the [ninjutsu]
 * helper — the cast is only offered after blockers are declared, charges {U}, returns the chosen
 * unblocked attacker to hand, and puts this creature onto the battlefield tapped and attacking the
 * same defender (CR 506.3a).
 *
 * "That player" is the player just dealt combat damage, and the ability does *not* target them —
 * `controlledByTriggeringPlayer()` scopes the creature target to that player's side of the board
 * (the Dreadmaw's Ire shape), which is narrower than "a creature an opponent controls" in a
 * multiplayer game. The `MayEffect` wrapper is what makes this a may-then-target trigger: the
 * engine asks the yes/no as the ability goes on the stack, then takes targets (CR 603.3d), and the
 * trigger is removed from the stack outright when the damaged player controls no creature.
 */
val MistbladeShinobi = card("Mistblade Shinobi") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Ninja"
    power = 1
    toughness = 1
    oracleText = "Ninjutsu {U} ({U}, Return an unblocked attacker you control to hand: Put this " +
        "card onto the battlefield from your hand tapped and attacking.)\n" +
        "Whenever this creature deals combat damage to a player, you may return target creature " +
        "that player controls to its owner's hand."

    ninjutsu("{U}")

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        val creature = target(
            "target creature that player controls",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.controlledByTriggeringPlayer()))
        )
        effect = MayEffect(Effects.ReturnToHand(creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c06f58f-0419-4637-8060-4a93670a4c68.jpg?1783944206"
    }
}
