package com.wingedsheep.mtg.sets.definitions.mor.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ChooseOptionEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.effects.OptionType
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Distant Melody
 * {3}{U}
 * Sorcery
 *
 * Choose a creature type. Draw a card for each permanent you control of that type.
 */
val DistantMelody = card("Distant Melody") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Choose a creature type. Draw a card for each permanent you control of that type."

    spell {
        effect = Effects.Composite(
            listOf(
                ChooseOptionEffect(
                    optionType = OptionType.CREATURE_TYPE,
                    storeAs = "chosenType",
                ),
                DrawCardsEffect(
                    count = DynamicAmount.AggregateBattlefield(
                        player = Player.You,
                        filter = GameObjectFilter.Permanent.withSubtypeFromVariable("chosenType"),
                    ),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Omar Rayyan"
        flavorText = "Oona's song is like a twisted dinner chime. All the faeries return home, but it " +
            "is Oona who feasts—on the stolen dreams and rumors they serve her."
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5a28e4f9-3b68-40bb-bf77-850b08c6b096.jpg?1783942800"
    }
}
