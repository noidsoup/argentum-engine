package com.wingedsheep.mtg.sets.definitions.mor.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Rhys the Exiled
 * {2}{G}
 * Legendary Creature — Elf Warrior
 * 3/2
 *
 * Whenever Rhys attacks, you gain 1 life for each Elf you control.
 * {B}, Sacrifice an Elf: Regenerate Rhys.
 */
val RhysTheExiled = card("Rhys the Exiled") {
    manaCost = "{2}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Elf Warrior"
    power = 3
    toughness = 2
    oracleText = "Whenever Rhys attacks, you gain 1 life for each Elf you control.\n" +
        "{B}, Sacrifice an Elf: Regenerate Rhys."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.GainLife(
            DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Creature.withSubtype("Elf"),
            ),
            EffectTarget.Controller,
        )
        description = "Whenever Rhys attacks, you gain 1 life for each Elf you control."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{B}"),
            Costs.Sacrifice(GameObjectFilter.Creature.withSubtype("Elf")),
        )
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{B}, Sacrifice an Elf: Regenerate Rhys."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "135"
        artist = "Steve Prescott"
        flavorText = "Once a famed hunter and packmaster, now a renegade seeking his own path."
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f26da029-9c95-4e9b-9a1b-62048c16acaf.jpg?1783942776"
    }
}
