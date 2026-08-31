package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Moon-Eating Dog — Global Series: Jiang Yanggu & Mu Yanling #10
 * {3}{U} · Creature — Dog · 3/3
 *
 * As long as you control a Yanling planeswalker, this creature has flying.
 */
val MoonEatingDog = card("Moon-Eating Dog") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Dog"
    power = 3
    toughness = 3
    oracleText =
        "As long as you control a Yanling planeswalker, this creature has flying. " +
            "(It can't be blocked except by creatures with flying or reach.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, GroupFilter.source()),
            condition = Conditions.YouControl(
                GameObjectFilter.Planeswalker.withSubtype("Yanling"),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "10"
        artist = "Tingting Yeh"
        flavorText = "The most nimble of creatures, it chases the most sacred luminence."
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a85747dd-84c0-4e31-964e-c455c96f87bf.jpg?1783934633"
    }
}
