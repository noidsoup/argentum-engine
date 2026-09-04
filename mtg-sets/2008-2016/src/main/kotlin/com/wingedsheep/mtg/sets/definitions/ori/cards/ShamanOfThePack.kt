package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Shaman of the Pack
 * {1}{B}{G}
 * Creature — Elf Shaman
 * 3/2
 *
 * When this creature enters, target opponent loses life equal to the number of Elves you control.
 *
 * X is read on resolution via [DynamicAmount.AggregateBattlefield], so a Shaman still on the
 * battlefield counts itself — the same shape as Lys Alana Scarblade's Elf tally.
 */
val ShamanOfThePack = card("Shaman of the Pack") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Elf Shaman"
    oracleText = "When this creature enters, target opponent loses life equal to the number of Elves you control."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.LoseLife(
            DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Permanent.withSubtype(Subtype.ELF),
            ),
            opponent,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "217"
        artist = "Dan Murayama Scott"
        flavorText = "To the elves, her spear is a compass; to the boggarts, a harbinger of doom."
        imageUri = "https://cards.scryfall.io/normal/front/3/0/304687b1-2294-4144-b2bd-7e36f9aaac34.jpg?1783938313"
    }
}
