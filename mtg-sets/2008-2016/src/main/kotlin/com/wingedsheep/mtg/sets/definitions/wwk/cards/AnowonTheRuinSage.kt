package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Anowon, the Ruin Sage
 * {3}{B}{B}
 * Legendary Creature — Vampire Shaman
 * 4/3
 *
 * At the beginning of your upkeep, each player sacrifices a non-Vampire creature of their choice.
 *
 * Canonical printing: Worldwake (earliest real printing). Commander reprints are [Printing] rows.
 * The upkeep trigger is [Triggers.YourUpkeep] (only on your turn), and "each player sacrifices"
 * routes through [Effects.Sacrifice] with [Player.ActivePlayerFirst] (APNAP order) — the same
 * edict shape as Liliana, Dreadhorde General's −4, filtered to non-Vampires like Ruthless
 * Winnower's non-Elf upkeep.
 */
val AnowonTheRuinSage = card("Anowon, the Ruin Sage") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Vampire Shaman"
    oracleText = "At the beginning of your upkeep, each player sacrifices a non-Vampire creature of their choice."
    power = 4
    toughness = 3

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Sacrifice(
            GameObjectFilter.Creature.notSubtype(Subtype.VAMPIRE),
            1,
            EffectTarget.PlayerRef(Player.ActivePlayerFirst),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "49"
        artist = "Dan Murayama Scott"
        flavorText = "\"So many have died in search of that map. And now it appears in the hands of the arrogant child Chandra Nalaar.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7b247681-1cf6-42a0-ad44-76261c690596.jpg?1783942058"
    }
}
