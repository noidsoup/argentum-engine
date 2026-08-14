package com.wingedsheep.mtg.sets.definitions.bok.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.LookAtTargetHandEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Walker of Secret Ways
 * {2}{U}
 * Creature — Human Ninja
 * 1/2
 *
 * Ninjutsu {1}{U} ({1}{U}, Return an unblocked attacker you control to hand: Put this card onto
 * the battlefield from your hand tapped and attacking.)
 * Whenever this creature deals combat damage to a player, look at that player's hand.
 * {1}{U}: Return target Ninja you control to its owner's hand. Activate only during your turn.
 *
 * Ninjutsu rides the shared declare-blockers alternative-cost pipeline via [ninjutsu]. The peek
 * uses [LookAtTargetHandEffect] on [Player.TriggeringPlayer] (the player just dealt combat
 * damage). The bounce is gated with [ActivationRestriction.OnlyDuringYourTurn].
 */
val WalkerOfSecretWays = card("Walker of Secret Ways") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Ninja"
    power = 1
    toughness = 2
    oracleText = "Ninjutsu {1}{U} ({1}{U}, Return an unblocked attacker you control to hand: " +
        "Put this card onto the battlefield from your hand tapped and attacking.)\n" +
        "Whenever this creature deals combat damage to a player, look at that player's hand.\n" +
        "{1}{U}: Return target Ninja you control to its owner's hand. Activate only during your turn."

    ninjutsu("{1}{U}")

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = LookAtTargetHandEffect(EffectTarget.PlayerRef(Player.TriggeringPlayer))
    }

    activatedAbility {
        cost = Costs.Mana("{1}{U}")
        val ninja = target(
            "target Ninja you control",
            TargetCreature(
                filter = TargetFilter(GameObjectFilter.Creature.withSubtype("Ninja").youControl())
            )
        )
        effect = Effects.ReturnToHand(ninja)
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "60"
        artist = "Scott M. Fischer"
        imageUri = "https://cards.scryfall.io/normal/front/3/4/34469923-9a48-4fa5-aff8-4e09926f039f.jpg?1783944200"

        ruling("2021-03-19", "The ninjutsu ability can be activated only after blockers have been declared. Before then, attacking creatures are neither blocked nor unblocked.")
        ruling("2021-03-19", "As you activate a ninjutsu ability, you reveal the Ninja card in your hand and return the attacking creature. The Ninja isn't put onto the battlefield until the ability resolves. If it leaves your hand before then, it won't enter the battlefield at all.")
    }
}
