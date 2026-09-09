package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sanctum Seeker
 * {2}{B}{B}
 * Creature — Vampire Knight
 * 3/4
 *
 * Whenever a Vampire you control attacks, each opponent loses 1 life and you gain 1 life.
 *
 * Canonical printing: Ixalan (XLN). VOC and later sets are [Printing] rows.
 *
 * Per-attacker trigger: [Triggers.attacks] with [TriggerBinding.ANY] over Vampires you control
 * (Balthier and Fran / Miriam idiom), not the once-per-combat [Triggers.YouAttackWithFilter]
 * batch used by Glass-Cast Heart.
 */
val SanctumSeeker = card("Sanctum Seeker") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Knight"
    oracleText = "Whenever a Vampire you control attacks, each opponent loses 1 life and you gain 1 life."
    power = 3
    toughness = 4

    triggeredAbility {
        trigger = Triggers.attacks(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE).youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1),
        )
        description = "Whenever a Vampire you control attacks, each opponent loses 1 life and you gain 1 life."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "120"
        artist = "Volkan Baǵa"
        flavorText = "The Sanctum Seekers were the first of the knightly orders to cross the sea and are still the most zealous."
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72150779-0bb6-4d20-a898-cda93a66e7cd.jpg?1783935756"
    }
}
