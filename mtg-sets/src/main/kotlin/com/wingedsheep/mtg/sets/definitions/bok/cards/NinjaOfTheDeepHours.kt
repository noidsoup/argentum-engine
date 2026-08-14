package com.wingedsheep.mtg.sets.definitions.bok.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity

/**
 * Ninja of the Deep Hours
 * {3}{U}
 * Creature — Human Ninja
 * 2/2
 *
 * Ninjutsu {1}{U} ({1}{U}, Return an unblocked attacker you control to hand: Put this card onto the
 * battlefield from your hand tapped and attacking.)
 * Whenever this creature deals combat damage to a player, you may draw a card.
 *
 * Ninjutsu rides the engine's shared declare-blockers alternative-cost pipeline via the [ninjutsu]
 * helper — the cast is only offered after blockers are declared, charges {1}{U}, returns the chosen
 * unblocked attacker to hand, and puts this creature onto the battlefield tapped and attacking the
 * same defender (CR 506.3a).
 *
 * The draw is untargeted, so `optional = true` is the whole "you may": the engine lowers it to a
 * resolution-time yes/no gate on the triggered ability.
 */
val NinjaOfTheDeepHours = card("Ninja of the Deep Hours") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Ninja"
    power = 2
    toughness = 2
    oracleText = "Ninjutsu {1}{U} ({1}{U}, Return an unblocked attacker you control to hand: Put " +
        "this card onto the battlefield from your hand tapped and attacking.)\n" +
        "Whenever this creature deals combat damage to a player, you may draw a card."

    ninjutsu("{1}{U}")

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        optional = true
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Dan Murayama Scott"
        imageUri = "https://cards.scryfall.io/normal/front/3/6/367a67c7-54db-4336-b55a-3fa27625172a.jpg?1783944206"

        ruling("2021-03-19", "The ninjutsu ability can be activated only after blockers have been declared. Before then, attacking creatures are neither blocked nor unblocked.")
        ruling("2021-03-19", "As you activate a ninjutsu ability, you reveal the Ninja card in your hand and return the attacking creature. The Ninja isn't put onto the battlefield until the ability resolves. If it leaves your hand before then, it won't enter the battlefield at all.")
        ruling("2021-03-19", "The creature put onto the battlefield with ninjutsu enters the battlefield attacking the same player or planeswalker that the returned creature was attacking. This is a rule specific to ninjutsu.")
        ruling("2021-03-19", "Although the Ninja is attacking, it was never declared as an attacking creature (for purposes of abilities that trigger whenever a creature attacks, for example).")
        ruling("2021-03-19", "The ninjutsu ability can be activated during the declare blockers step, combat damage step, or end of combat step. If you wait until after the declare blockers step, because all combat damage is dealt at once, the Ninja won't normally deal combat damage.")
    }
}
