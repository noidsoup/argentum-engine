package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ruthless Winnower — Kaldheim Commander #10
 * {3}{B}{B} · Creature — Elf Rogue · 4/4
 *
 * At the beginning of each player's upkeep, that player sacrifices a non-Elf creature of their choice.
 *
 * Same upkeep sacrifice shape as Call to the Grave and Molder Slug: [Triggers.EachUpkeep] hands
 * off to the triggering player, and [ForceSacrificeEffect] enforces the filter plus the chooser.
 */
val RuthlessWinnower = card("Ruthless Winnower") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elf Rogue"
    oracleText = "At the beginning of each player's upkeep, that player sacrifices a non-Elf creature of their choice."
    power = 4
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EachUpkeep
        effect = ForceSacrificeEffect(
            GameObjectFilter.Creature.notSubtype(Subtype.ELF),
            1,
            EffectTarget.PlayerRef(Player.TriggeringPlayer),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "10"
        artist = "Zoltan Boros"
        flavorText = "\"I count the usurpers with my dagger, one death blow at a time.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/8/886ef13a-4f9d-425b-a071-6366cdb637c8.jpg?1783928337"
    }
}
