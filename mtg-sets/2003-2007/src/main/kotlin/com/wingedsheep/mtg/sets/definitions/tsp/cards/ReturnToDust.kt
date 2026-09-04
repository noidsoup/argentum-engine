package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetOther

/**
 * Return to Dust
 * {2}{W}{W}
 * Instant
 *
 * Exile target artifact or enchantment. If you cast this spell during your main phase, you may
 * exile up to one other target artifact or enchantment.
 *
 * "During your main phase" is frozen at cast time via [captureAtCast] +
 * [Conditions.CapturedAtCast]; the optional second exile is gated on that flag and on a second
 * target actually being chosen ([Conditions.TargetMatchesFilter]).
 */
val ReturnToDust = card("Return to Dust") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText =
        "Exile target artifact or enchantment. If you cast this spell during your main phase, " +
            "you may exile up to one other target artifact or enchantment."

    spell {
        captureAtCast("mainPhase", Conditions.IsYourMainPhase)
        val first = target("target artifact or enchantment", Targets.ArtifactOrEnchantment)
        val second = target(
            "up to one other target artifact or enchantment",
            TargetOther(TargetObject(optional = true, filter = TargetFilter.ArtifactOrEnchantment)),
        )
        effect = Effects.Exile(first).then(
            ConditionalEffect(
                condition = Conditions.All(
                    Conditions.CapturedAtCast("mainPhase"),
                    Conditions.TargetMatchesFilter(GameObjectFilter.ArtifactOrEnchantment, targetIndex = 1),
                ),
                effect = Effects.Exile(second),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "39"
        artist = "Wayne Reynolds"
        flavorText = "Some timelines forever fray, branch, and intermingle. Others end abruptly."
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48185c8f-ac41-46e2-85b1-760abac914ac.jpg?1783943250"
    }
}
