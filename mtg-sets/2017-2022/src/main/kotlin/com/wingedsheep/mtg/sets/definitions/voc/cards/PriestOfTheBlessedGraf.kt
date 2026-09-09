package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Priest of the Blessed Graf
 * {2}{W}
 * Creature — Human Cleric
 * 1/2
 *
 * At the beginning of your end step, create X 1/1 white Spirit creature tokens with flying, where
 * X is the number of opponents who control more lands than you.
 *
 * The per-opponent count is [DynamicAmount.CountPlayersWith] over [Player.EachOpponent]: inside the
 * loop, `Player.You` is rebound to each candidate opponent and [Player.ControllerOfSource] stays
 * this creature's controller, so the comparison is "that opponent's lands > your lands" for each
 * seat. Zero qualifying opponents → X = 0 and no tokens are created.
 */
val PriestOfTheBlessedGraf = card("Priest of the Blessed Graf") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "At the beginning of your end step, create X 1/1 white Spirit creature tokens " +
        "with flying, where X is the number of opponents who control more lands than you."
    power = 1
    toughness = 2

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.CreateToken(
            count = DynamicAmount.CountPlayersWith(
                scope = Player.EachOpponent,
                condition = Conditions.CompareAmounts(
                    left = DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Land),
                    operator = ComparisonOperator.GT,
                    right = DynamicAmount.AggregateBattlefield(
                        Player.ControllerOfSource,
                        GameObjectFilter.Land,
                    ),
                ),
            ),
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/8/3/83497714-97ae-4846-8096-f7f1524f0e09.jpg?1783924702",
        )
        description = "At the beginning of your end step, create X 1/1 white Spirit creature tokens " +
            "with flying, where X is the number of opponents who control more lands than you."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "7"
        artist = "Irina Nordsol"
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5b3947c1-f497-4407-ae29-287a490cceee.jpg?1783925007"
    }
}
