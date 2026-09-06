package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Wolverine Riders — Kaldheim Commander (KHC) #14
 * {4}{G}{G} · Creature — Elf Warrior · 4/4
 *
 * At the beginning of each upkeep, create a 1/1 green Elf Warrior creature token.
 * Whenever another Elf you control enters, you gain life equal to its toughness.
 *
 * "Another Elf you control" is an OTHER-bound enters trigger over `Elf.youControl()` — the
 * Riders' own ETB does not gain life from itself. Upkeep tokens are Elves and do trigger the
 * second ability. [DynamicAmounts.triggeringToughness] reads the entering permanent's toughness
 * at resolution (including counters and continuous effects already applied on entry).
 */
val WolverineRiders = card("Wolverine Riders") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 4
    toughness = 4
    oracleText = "At the beginning of each upkeep, create a 1/1 green Elf Warrior creature token.\n" +
        "Whenever another Elf you control enters, you gain life equal to its toughness."

    triggeredAbility {
        trigger = Triggers.EachUpkeep
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elf", "Warrior"),
            imageUri = "https://cards.scryfall.io/normal/front/1/1/118d0655-5719-4512-8bc1-fe759669811b.jpg?1783928078",
        )
        description = "At the beginning of each upkeep, create a 1/1 green Elf Warrior creature token."
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Any.withSubtype(Subtype.ELF).youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.GainLife(DynamicAmounts.triggeringToughness())
        description = "Whenever another Elf you control enters, you gain life equal to its toughness."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "14"
        artist = "Jesper Ejsing"
        flavorText = "\"We'll break their lines. The rest of you, follow!\""
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70fd0439-294b-454c-b2af-e814b85f4590.jpg?1783928337"
    }
}
