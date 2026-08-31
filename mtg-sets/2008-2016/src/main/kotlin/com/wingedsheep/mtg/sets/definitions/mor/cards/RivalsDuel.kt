package com.wingedsheep.mtg.sets.definitions.mor.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Rivals' Duel
 * {3}{R}
 * Sorcery
 *
 * Choose two target creatures that share no creature types. Those creatures fight each other.
 */
val RivalsDuel = card("Rivals' Duel") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose two target creatures that share no creature types. Those creatures fight each other. (Each deals damage equal to its power to the other.)"

    spell {
        target(
            "two target creatures that share no creature types",
            TargetCreature(
                count = 2,
                filter = TargetFilter.Creature,
                noSharedCreatureType = true,
            ),
        )
        effect = Effects.Fight(EffectTarget.ContextTarget(0), EffectTarget.ContextTarget(1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "99"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "They could agree on one thing only: one of them must die."
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d2eee8b-cc55-4a65-983f-0a91d7e4494a.jpg?1783942785"
    }
}
