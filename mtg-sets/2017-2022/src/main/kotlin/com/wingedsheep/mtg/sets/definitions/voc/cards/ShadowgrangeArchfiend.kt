package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.costs.CostAtom
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shadowgrange Archfiend
 * {6}{B}
 * Creature — Demon
 * 8/4
 *
 * When this creature enters, each opponent sacrifices a creature with the greatest power among
 * creatures they control. You gain life equal to the greatest power among creatures sacrificed
 * this way.
 * Madness—{2}{B}, Pay 8 life.
 *
 * The ETB is Extract a Confession's greatest-power edict plus
 * [DynamicAmounts.greatestPowerSacrificedThisWay] in the same [CompositeEffect] — the sacrifice
 * snapshots last-known power before the zone change, and the life gain reads the max across them.
 * Madness bundles [CostAtom.PayLife] as an additional cost on top of the fixed {2}{B} alternative.
 */
val ShadowgrangeArchfiend = card("Shadowgrange Archfiend") {
    manaCost = "{6}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    oracleText =
        "When this creature enters, each opponent sacrifices a creature with the greatest power " +
            "among creatures they control. You gain life equal to the greatest power among " +
            "creatures sacrificed this way.\n" +
            "Madness—{2}{B}, Pay 8 life. (If you discard this card, discard it into exile. When " +
            "you do, cast it for its madness cost or put it into your graveyard.)"
    power = 8
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CompositeEffect(
            listOf(
                Effects.Sacrifice(
                    GameObjectFilter.Creature.hasGreatestPower(),
                    target = EffectTarget.PlayerRef(Player.EachOpponent),
                ),
                Effects.GainLife(DynamicAmounts.greatestPowerSacrificedThisWay()),
            )
        )
    }

    madness("{2}{B}", AdditionalCost.Atom(CostAtom.PayLife(8)))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "22"
        artist = "Oleksandr Kozachenko"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de4e445f-4d54-44a8-a5f5-ef02b3d57251.jpg?1783925001"
    }
}
