package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Fire-Omen Crane — Global Series: Jiang Yanggu & Mu Yanling #29
 * {3}{R}{R} · Creature — Bird Spirit · 3/3
 *
 * Flying
 * Whenever this creature attacks, it deals 1 damage to target creature an opponent controls.
 */
val FireOmenCrane = card("Fire-Omen Crane") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Bird Spirit"
    power = 3
    toughness = 3
    oracleText =
        "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
            "Whenever this creature attacks, it deals 1 damage to target creature an opponent controls."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        val t = target(
            "target creature an opponent controls",
            TargetCreature(filter = TargetFilter.Creature.opponentControls()),
        )
        effect = Effects.DealDamage(1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "29"
        artist = "Tan Yan Yao"
        flavorText = "If you can see it clearly, you're already on fire."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a24c9c98-9eff-4f4b-93ca-9281d61e6835.jpg?1783934625"
    }
}
