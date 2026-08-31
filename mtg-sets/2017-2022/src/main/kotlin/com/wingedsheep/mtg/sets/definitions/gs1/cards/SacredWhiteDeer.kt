package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Sacred White Deer — Global Series: Jiang Yanggu & Mu Yanling #25
 * {1}{G} · Creature — Elk · 2/2
 *
 * {3}{G}, {T}: You gain 4 life. Activate only if you control a Yanggu planeswalker.
 */
val SacredWhiteDeer = card("Sacred White Deer") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elk"
    power = 2
    toughness = 2
    oracleText =
        "{3}{G}, {T}: You gain 4 life. Activate only if you control a Yanggu planeswalker."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{G}"), Costs.Tap)
        effect = Effects.GainLife(4)
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.YouControl(GameObjectFilter.Planeswalker.withSubtype("Yanggu")),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "25"
        artist = "G-host Lee"
        flavorText =
            "The pale deer are believed to have lived a thousand years, the pure white five hundred more. " +
                "Those who seek them would gladly wander the wilds forever for the merest glimpse of these sacred beings."
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a864ae8-18fc-4c70-8564-65b745aded9a.jpg?1783934627"
    }
}
