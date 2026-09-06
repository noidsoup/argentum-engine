package com.wingedsheep.mtg.sets.definitions.c19.cards

import com.wingedsheep.sdk.core.Zone
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
 * Voice of Many
 * {2}{G}{G}
 * Creature — Elf Druid
 * 3/3
 * When this creature enters, draw a card for each opponent who controls fewer creatures than you.
 *
 * The per-opponent count is [DynamicAmount.CountPlayersWith] over [Player.EachOpponent]: inside the
 * loop, `Player.You` is rebound to the candidate opponent and [Player.ControllerOfSource] stays the
 * Druid's controller, so the comparison is "that opponent's creatures < your creatures" for each
 * opponent. Creature counts are read on resolution, so Voice of Many itself is on the battlefield
 * and included in your total when the draw runs.
 */
val VoiceOfMany = card("Voice of Many") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    oracleText = "When this creature enters, draw a card for each opponent who controls fewer creatures than you."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(
            DynamicAmount.CountPlayersWith(
                scope = Player.EachOpponent,
                condition = Conditions.CompareAmounts(
                    left = DynamicAmount.Count(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Creature),
                    operator = ComparisonOperator.LT,
                    right = DynamicAmount.Count(
                        Player.ControllerOfSource,
                        Zone.BATTLEFIELD,
                        GameObjectFilter.Creature,
                    ),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "36"
        artist = "Greg Staples"
        flavorText = "His words echo every roar, screech, and howl of the wilds."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f82328e9-fb4b-4dc9-afd6-fa210e4330bf.jpg?1783932801"
    }
}
