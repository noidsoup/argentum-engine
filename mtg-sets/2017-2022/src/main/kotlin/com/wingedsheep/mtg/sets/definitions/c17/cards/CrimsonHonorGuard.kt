package com.wingedsheep.mtg.sets.definitions.c17.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Crimson Honor Guard
 * {3}{R}{R}
 * Creature — Vampire Knight
 * 4/5
 *
 * Trample
 * At the beginning of each player's end step, this creature deals 4 damage to that player unless
 * they control a commander.
 */
val CrimsonHonorGuard = card("Crimson Honor Guard") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire Knight"
    oracleText =
        "Trample\n" +
            "At the beginning of each player's end step, this creature deals 4 damage to that " +
            "player unless they control a commander."
    power = 4
    toughness = 5

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EachEndStep
        effect = Effects.DealDamageUnless(
            unless = Conditions.PlayerControlsCommander(Player.TriggeringPlayer),
            amount = 4,
            target = EffectTarget.PlayerRef(Player.TriggeringPlayer),
            damageSource = EffectTarget.Self,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "23"
        artist = "Kieran Yanner"
        flavorText =
            "The finest soldiers aren't worth a whit without a set of hands to lead them and a " +
                "set of eyes to guide them."
        imageUri = "https://cards.scryfall.io/normal/front/9/5/9597255c-2657-40e4-855f-c8701a70aa39.jpg?1783935943"
    }
}
