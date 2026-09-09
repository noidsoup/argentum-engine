package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CopyExceptions
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Donal, Herald of Wings
 * {2}{U}{U}
 * Legendary Creature — Human Wizard
 * 3/3
 *
 * Whenever you cast a nonlegendary creature spell with flying, you may copy it, except the copy is
 * a 1/1 Spirit in addition to its other types. Do this only once each turn. (The copy becomes a
 * token.)
 *
 * Canonical printing: Innistrad: Crimson Vow Commander (VOC) — earliest non-promo printing.
 * The cast trigger is [Triggers.youCastSpell] over nonlegendary creatures with flying;
 * [MayEffect] + [Effects.CopyTargetSpell] on [EffectTarget.TriggeringEntity] with
 * [CopyExceptions] for the 1/1 Spirit riders (the Donal / Herald of Wings pattern).
 */
val DonalHeraldOfWings = card("Donal, Herald of Wings") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Wizard"
    oracleText = "Whenever you cast a nonlegendary creature spell with flying, you may copy it, " +
        "except the copy is a 1/1 Spirit in addition to its other types. Do this only once each " +
        "turn. (The copy becomes a token.)"
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Creature.nonlegendary().withKeyword(Keyword.FLYING),
        )
        effectOncePerTurn = true
        effect = MayEffect(
            Effects.CopyTargetSpell(
                target = EffectTarget.TriggeringEntity,
                exceptions = CopyExceptions(
                    powerOverride = 1,
                    toughnessOverride = 1,
                    addedSubtypes = setOf(Subtype.SPIRIT),
                ),
            ),
        )
        description = "Whenever you cast a nonlegendary creature spell with flying, you may copy it, " +
            "except the copy is a 1/1 Spirit in addition to its other types. Do this only once " +
            "each turn."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "3"
        artist = "Wayne Reynolds"
        flavorText = "He sees a flicker of Avacyn's grace in every wingbeat."
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70cf8d2b-5220-4fe9-b39d-6700f0b27cca.jpg?1783925009"
    }
}
