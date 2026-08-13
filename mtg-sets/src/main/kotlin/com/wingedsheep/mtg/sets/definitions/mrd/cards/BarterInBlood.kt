package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Barter in Blood
 * {2}{B}{B}
 * Sorcery
 * Each player sacrifices two creatures of their choice.
 *
 * Same ForEachPlayer + ForceSacrifice shape as Abyssal Gorestalker's ETB.
 */
val BarterInBlood = card("Barter in Blood") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Each player sacrifices two creatures of their choice."

    spell {
        effect = ForEachPlayerEffect(
            players = Player.Each,
            effects = listOf(
                ForceSacrificeEffect(
                    filter = GameObjectFilter.Creature,
                    count = 2,
                    target = EffectTarget.Controller,
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "57"
        artist = "Paolo Parente"
        flavorText =
            "\"In the game of conquest, who cares about the pawns if the king yet reigns?\"\n—Geth, keeper of the Vault"
        imageUri =
            "https://cards.scryfall.io/normal/front/b/e/beccbb2c-ca1d-4b72-9eca-a64a313fd830.jpg?1783944550"
    }
}
