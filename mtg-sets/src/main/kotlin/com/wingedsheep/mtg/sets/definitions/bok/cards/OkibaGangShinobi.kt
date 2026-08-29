package com.wingedsheep.mtg.sets.definitions.bok.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Okiba-Gang Shinobi
 * {3}{B}{B}
 * Creature — Rat Ninja
 * 3/2
 *
 * Ninjutsu {3}{B}
 * Whenever this creature deals combat damage to a player, that player discards two cards.
 */
val OkibaGangShinobi = card("Okiba-Gang Shinobi") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Rat Ninja"
    power = 3
    toughness = 2
    oracleText = "Ninjutsu {3}{B} ({3}{B}, Return an unblocked attacker you control to hand: Put this " +
        "card onto the battlefield from your hand tapped and attacking.)\n" +
        "Whenever this creature deals combat damage to a player, that player discards two cards."

    ninjutsu("{3}{B}")

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Patterns.Hand.discardCards(
            count = 2,
            target = EffectTarget.PlayerRef(Player.DefendingPlayer),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5cd9297e-301e-4e70-af9b-3218eacacf8d.jpg?1783944195"
    }
}
