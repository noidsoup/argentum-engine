package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ChooseCreatureTypeEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Pact of the Serpent — Kaldheim Commander (KHC) #9
 * {1}{B}{B} · Sorcery
 *
 * Choose a creature type. Target player draws X cards and loses X life, where X is the number of
 * creatures they control of the chosen type.
 *
 * [ChooseCreatureTypeEffect] stamps the resolution-time pick as `chosenCreatureType`; the count and
 * both payoffs read that type on the targeted player's battlefield via [Player.ContextPlayer](0).
 */
private const val CHOSEN_TYPE = "chosenCreatureType"

private val creaturesOfChosenTypeTargetControls = DynamicAmount.AggregateBattlefield(
    player = Player.ContextPlayer(0),
    filter = GameObjectFilter.Creature.withSubtypeFromVariable(CHOSEN_TYPE),
)

val PactOfTheSerpent = card("Pact of the Serpent") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Choose a creature type. Target player draws X cards and loses X life, where X is " +
        "the number of creatures they control of the chosen type."

    spell {
        target("target player", TargetPlayer())
        effect = Effects.Composite(
            listOf(
                ChooseCreatureTypeEffect,
                DrawCardsEffect(
                    count = creaturesOfChosenTypeTargetControls,
                    target = EffectTarget.ContextTarget(0),
                ),
                Effects.LoseLife(
                    creaturesOfChosenTypeTargetControls,
                    EffectTarget.ContextTarget(0),
                ),
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "9"
        artist = "Donato Giancola"
        flavorText = "\"Our quarrels behind us. Our foes before us. Death or triumph for us all!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c5b3d118-ba4b-4a93-92a2-cd763de49d29.jpg?1783928337"
    }
}
