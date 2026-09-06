package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * End-Raze Forerunners
 * {5}{G}{G}{G}
 * Creature — Boar
 * 7/7
 * Vigilance, trample, haste
 * When this creature enters, other creatures you control get +2/+2 and gain vigilance and trample
 * until end of turn.
 *
 * The enters trigger is the Pyrewood Gearhulk shape: [Effects.ForEachInGroup] over
 * [GroupFilter] creatures you control with [excludeSelf] so the boar does not buff itself,
 * and each iteration applies +2/+2 plus keyword grants to [EffectTarget.Self] (the current
 * creature in the loop). [Effects.ModifyStats] and [Effects.GrantKeyword] default to until
 * end of turn.
 */
val EndRazeForerunners = card("End-Raze Forerunners") {
    manaCost = "{5}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Boar"
    power = 7
    toughness = 7
    oracleText = "Vigilance, trample, haste\n" +
        "When this creature enters, other creatures you control get +2/+2 and gain vigilance " +
        "and trample until end of turn."

    keywords(Keyword.VIGILANCE, Keyword.TRAMPLE, Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl(), excludeSelf = true),
            Effects.Composite(
                Effects.ModifyStats(2, 2, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "124"
        artist = "Mathias Kollros"
        flavorText = "\"They'll run the rest of the herd off the cliffs. Just make sure you're " +
            "well clear of them.\"\n—Domri Rade"
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a50d79fe-6d37-42f3-b7b0-0c3018282fa2.jpg?1783933671"
    }
}
