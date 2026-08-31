package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.targets.TargetOther

/**
 * Arc Trail
 * {1}{R}
 * Sorcery
 *
 * Arc Trail deals 2 damage to any target and 1 damage to any other target.
 *
 * Two independent target requirements: the second is wrapped in [TargetOther] so the "any other
 * target" clause is enforced at cast time — the two halves can't be pointed at the same creature,
 * player, planeswalker or battle. Each half is dealt separately during resolution, so if one target
 * becomes illegal the spell still resolves and the remaining half is dealt.
 */
val ArcTrail = card("Arc Trail") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Arc Trail deals 2 damage to any target and 1 damage to any other target."

    spell {
        val first = target("first target", AnyTarget(descriptionOverride = "any target (takes 2 damage)"))
        val second = target(
            "second target",
            TargetOther(AnyTarget(descriptionOverride = "any other target (takes 1 damage)"))
        )
        effect = Effects.DealDamage(2, first)
            .then(Effects.DealDamage(1, second))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "81"
        artist = "Marc Simonetti"
        flavorText = "\"Don't try to hit your enemies. Concentrate on the space between them, and " +
            "fill the air with doom.\"\n—Spear-Tribe teaching"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/445e3a0a-29a7-4dc0-80fe-569b9e751db3.jpg?1783941727"
    }
}
