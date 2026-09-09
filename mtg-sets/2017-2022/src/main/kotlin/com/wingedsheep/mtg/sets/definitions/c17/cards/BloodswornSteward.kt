package com.wingedsheep.mtg.sets.definitions.c17.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Bloodsworn Steward
 * {2}{R}{R}
 * Creature — Vampire Knight
 * 4/4
 *
 * Flying
 * Commander creatures you control get +2/+2 and have haste.
 *
 * Canonical printing — Commander 2017. VOC and later sets are [Printing] rows only.
 */
private val commanderCreaturesYouControl = GroupFilter(
    GameObjectFilter.Creature.commander().youControl(),
)

val BloodswornSteward = card("Bloodsworn Steward") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire Knight"
    oracleText = "Flying\nCommander creatures you control get +2/+2 and have haste."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 2,
            filter = commanderCreaturesYouControl,
        )
    }
    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, commanderCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "22"
        artist = "Daarken"
        flavorText =
            "\"My liege will lead and I will follow—to the battlefield, to the grave, and beyond.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e185f493-57c3-47dd-8dac-de8690ce8fbc.jpg?1783935943"
    }
}
