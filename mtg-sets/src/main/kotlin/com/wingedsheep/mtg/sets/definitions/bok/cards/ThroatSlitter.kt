package com.wingedsheep.mtg.sets.definitions.bok.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Throat Slitter
 * {4}{B}
 * Creature — Rat Ninja
 * 2/2
 *
 * Ninjutsu {2}{B}
 * Whenever this creature deals combat damage to a player, destroy target nonblack creature that
 * player controls.
 *
 * "That player" is the player just dealt combat damage — `controlledByTriggeringPlayer()` scopes
 * the creature target to that player's side of the board (the Mistblade Shinobi / Dreadmaw's Ire
 * shape). The destroy is mandatory once the trigger resolves with a legal target.
 */
val ThroatSlitter = card("Throat Slitter") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Rat Ninja"
    power = 2
    toughness = 2
    oracleText = "Ninjutsu {2}{B} ({2}{B}, Return an unblocked attacker you control to hand: Put this " +
        "card onto the battlefield from your hand tapped and attacking.)\n" +
        "Whenever this creature deals combat damage to a player, destroy target nonblack creature " +
        "that player controls."

    ninjutsu("{2}{B}")

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        val creature = target(
            "target nonblack creature that player controls",
            TargetCreature(
                filter = TargetFilter(
                    GameObjectFilter.Creature.controlledByTriggeringPlayer().notColor(Color.BLACK),
                ),
            ),
        )
        effect = Effects.Move(creature, Zone.GRAVEYARD, byDestruction = true)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "88"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4dcfdb4-c2ff-4fc5-aa2a-0c6e311d2910.jpg?1783944194"

        ruling("2021-03-19", "The ninjutsu ability can be activated only after blockers have been declared.")
        ruling("2021-03-19", "As you activate a ninjutsu ability, you reveal the Ninja card in your hand and return the attacking creature. The Ninja isn't put onto the battlefield until the ability resolves.")
    }
}
