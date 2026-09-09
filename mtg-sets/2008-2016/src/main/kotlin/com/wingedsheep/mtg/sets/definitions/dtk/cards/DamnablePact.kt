package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Damnable Pact — Dragons of Tarkir (DTK) #93
 * {X}{B}{B} · Sorcery
 *
 * Target player draws X cards and loses X life.
 *
 * Both payoffs read the same [DynamicAmount.XValue] chosen when the spell was cast — the 2007-10-01
 * Profane Command ruling shape ("the value chosen for X applies to each X in the spell's effect").
 */
val DamnablePact = card("Damnable Pact") {
    manaCost = "{X}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Target player draws X cards and loses X life."

    spell {
        target("target player", Targets.Player)
        effect = Effects.Composite(
            Effects.DrawCards(DynamicAmount.XValue, EffectTarget.ContextTarget(0)),
            Effects.LoseLife(DynamicAmount.XValue, EffectTarget.ContextTarget(0)),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "93"
        artist = "Zack Stella"
        flavorText = "\"Silumgar's mind is a dark labyrinth, full of grim secrets and subtle traps.\"\n" +
            "—Siara, the Dragon's Mouth"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f65bcf62-48c3-4e4d-b129-8e03b3635326.jpg?1783938599"
        ruling(
            "2007-10-01",
            "The value chosen for X applies to each X in the spell's effect. You pay {X} only once."
        )
    }
}
