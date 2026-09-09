package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanentOrPlayer
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Avacyn's Judgment (Shadows over Innistrad #145)
 * {1}{R}
 * Sorcery
 *
 * Madness {X}{R}
 * Avacyn's Judgment deals 2 damage divided as you choose among any number of targets. If this
 * spell's madness cost was paid, it deals X damage divided as you choose among those permanents
 * and/or players instead.
 *
 * The normal cast uses [AnyTarget] plus 2 divided damage. The madness branch mirrors Fight with
 * Fire's kicked shape — alternate `kickerTarget` / `kickerEffect` slots that the engine selects
 * when [com.wingedsheep.sdk.scripting.conditions.MadnessCostWasPaid] applies at cast time.
 */
val AvacynsJudgment = card("Avacyn's Judgment") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Madness {X}{R} (If you discard this card, discard it into exile. When you do, " +
        "cast it for its madness cost or put it into your graveyard.)\n" +
        "Avacyn's Judgment deals 2 damage divided as you choose among any number of targets. If " +
        "this spell's madness cost was paid, it deals X damage divided as you choose among those " +
        "permanents and/or players instead."

    madness("{X}{R}")

    spell {
        target = AnyTarget(count = 2, minCount = 0, optional = true)
        effect = Effects.DividedDamage(total = 2, minTargets = 0, maxTargets = 2)

        kickerTarget = TargetPermanentOrPlayer(
            count = 20,
            optional = true,
            descriptionOverride = "any number of target permanents and/or players",
        )
        kickerEffect = Effects.DividedDamage(
            dynamicTotal = DynamicAmount.XValue,
            minTargets = 0,
            maxTargets = 20,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "145"
        artist = "Victor Adame Minguez"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c5f44ce-1464-4282-9afa-20e9ea44c613.jpg?1783937759"
    }
}
