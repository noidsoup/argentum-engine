package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.soulbond
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Spectral Gateguards
 * {4}{W}
 * Creature — Spirit Soldier
 * 2/5
 * Soulbond (You may pair this creature with another unpaired creature when either enters.
 *   They remain paired for as long as you control both of them.)
 * As long as this creature is paired with another creature, both creatures have vigilance.
 *
 * Canonical printing: AVR #37 (oracle_id 334f7cd5-…).
 */
val SpectralGateguards = card("Spectral Gateguards") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Soldier"
    oracleText =
        "Soulbond (You may pair this creature with another unpaired creature when either enters. " +
            "They remain paired for as long as you control both of them.)\n" +
            "As long as this creature is paired with another creature, both creatures have vigilance."
    power = 2
    toughness = 5

    soulbond()

    staticAbility {
        condition = Conditions.SourceIsPaired
        ability = GrantKeyword(Keyword.VIGILANCE, Filters.Self)
    }
    staticAbility {
        condition = Conditions.SourceIsPaired
        ability = GrantKeyword(Keyword.VIGILANCE, Filters.SoulbondPartner)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Wayne England"
        imageUri =
            "https://cards.scryfall.io/normal/front/f/7/f774e0eb-5c05-4a9e-8ab7-9ee4c7741591.jpg?1783940729"
    }
}
