package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Heavenly Qilin — Global Series: Jiang Yanggu & Mu Yanling #6
 * {2}{W} · Creature — Kirin · 2/2
 *
 * Flying
 * Whenever this creature attacks, another target creature you control gains flying until end of turn.
 */
val HeavenlyQilin = card("Heavenly Qilin") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kirin"
    power = 2
    toughness = 2
    oracleText =
        "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
            "Whenever this creature attacks, another target creature you control gains flying until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        val t = target(
            "another target creature you control",
            TargetCreature(filter = TargetFilter.OtherCreatureYouControl),
        )
        effect = Effects.GrantKeyword(Keyword.FLYING, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "6"
        artist = "Xin-Yu Liu"
        flavorText =
            "It strides through the universe as though stepping on the stars, " +
                "crossing freely between the celestial and earthly worlds."
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f1cb6fba-e314-4270-bbba-fa0584530f72.jpg?1783934634"
    }
}
