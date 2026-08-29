package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Mass Mutiny
 * {3}{R}{R}
 * Sorcery
 *
 * For each opponent, gain control of up to one target creature that player controls until end of
 * turn. Untap those creatures. They gain haste until end of turn.
 *
 * Per-opponent targeting follows the Blatant Thievery corpus shape: a single optional creature
 * target controlled by an opponent (`TargetFilter.CreatureOpponentControls`), which is exactly
 * one steal in 1v1 and generalizes to one per opponent in multiplayer.
 */
val MassMutiny = card("Mass Mutiny") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "For each opponent, gain control of up to one target creature that player controls " +
        "until end of turn. Untap those creatures. They gain haste until end of turn."

    spell {
        val creature = target(
            "target creature",
            TargetCreature(optional = true, filter = TargetFilter.CreatureOpponentControls),
        )
        effect = Effects.Composite(
            Effects.GainControl(creature, Duration.EndOfTurn),
            Effects.Untap(creature),
            Effects.GrantKeyword(Keyword.HASTE, creature),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "48"
        artist = "Carl Critchlow"
        flavorText = "\"What say you, my most trusted advisors? . . . Advisors?\"\n—Edra, merfolk sovereign"
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d18e6fb-e4ae-4ff4-9412-f16edb2e56c6.jpg?1783940618"
    }
}
