package com.wingedsheep.mtg.sets.definitions.c14.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Benevolent Offering
 * {3}{W}
 * Instant
 *
 * • Choose an opponent. You and that player each create three 1/1 white Spirit creature tokens
 *   with flying.
 * • Choose an opponent. You gain 2 life for each creature you control and that player gains 2 life
 *   for each creature they control.
 *
 * Each bullet is its own mode with a fresh [Effects.ChooseOpponent] prefix so the opponent pick is
 * recorded on the spell's `ChoiceSlot.OPPONENT` before the mode body reads it back through
 * [Player.ChosenOpponent] (Oath of the Grey Host / Boreas Charger composition).
 *
 * Canonical earliest printing: Commander 2014 (C14). VOC and other sets are reprints.
 */
val BenevolentOffering = card("Benevolent Offering") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Choose an opponent. You and that player each create three 1/1 white Spirit " +
        "creature tokens with flying.\n" +
        "Choose an opponent. You gain 2 life for each creature you control and that player gains " +
        "2 life for each creature they control."

    val spiritTokenImage =
        "https://cards.scryfall.io/normal/front/8/3/83497714-97ae-4846-8096-f7f1524f0e09.jpg?1783924702"

    val spiritTokensForYou = Effects.CreateToken(
        power = 1,
        toughness = 1,
        colors = setOf(Color.WHITE),
        creatureTypes = setOf("Spirit"),
        keywords = setOf(Keyword.FLYING),
        count = 3,
        imageUri = spiritTokenImage,
    )

    val spiritTokensForOpponent = Effects.CreateToken(
        power = 1,
        toughness = 1,
        colors = setOf(Color.WHITE),
        creatureTypes = setOf("Spirit"),
        keywords = setOf(Keyword.FLYING),
        count = 3,
        controller = EffectTarget.PlayerRef(Player.ChosenOpponent),
        imageUri = spiritTokenImage,
    )

    val lifePerCreatureYouControl = Effects.GainLife(
        DynamicAmount.Multiply(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature),
            2,
        ),
    )

    val lifePerCreatureTheyControl = Effects.GainLife(
        DynamicAmount.Multiply(
            DynamicAmount.AggregateBattlefield(Player.ChosenOpponent, GameObjectFilter.Creature),
            2,
        ),
        EffectTarget.PlayerRef(Player.ChosenOpponent),
    )

    spell {
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                Effects.ChooseOpponent("Choose an opponent")
                    .then(spiritTokensForYou)
                    .then(spiritTokensForOpponent),
                "Choose an opponent. You and that player each create three 1/1 white Spirit " +
                    "creature tokens with flying.",
            ),
            Mode.noTarget(
                Effects.ChooseOpponent("Choose an opponent")
                    .then(lifePerCreatureYouControl)
                    .then(lifePerCreatureTheyControl),
                "Choose an opponent. You gain 2 life for each creature you control and that " +
                    "player gains 2 life for each creature they control.",
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "3"
        artist = "Ryan Yee"
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a1603aa-bf6f-4a9d-b370-22f2286d0d36.jpg?1783938874"
    }
}
