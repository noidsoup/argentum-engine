package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Vivid Flying Fish — Global Series: Jiang Yanggu & Mu Yanling #4
 * {1}{U} · Creature — Fish Lizard · 1/1
 *
 * This creature has flying as long as it's attacking.
 */
val VividFlyingFish = card("Vivid Flying Fish") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Fish Lizard"
    power = 1
    toughness = 1
    oracleText =
        "This creature has flying as long as it's attacking. " +
            "(It can't be blocked except by creatures with flying or reach.)"

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING, GroupFilter.source())
        condition = Conditions.SourceIsAttacking
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Tingting Yeh"
        flavorText =
            "The rainbow seen above the Great Lake of Cloud Dream just after the rain is nothing but " +
                "the vivid-colored flying fishes' ritual dance."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c6783b19-4d23-445c-9c4c-bb485a1e4149.jpg?1783934635"
    }
}
