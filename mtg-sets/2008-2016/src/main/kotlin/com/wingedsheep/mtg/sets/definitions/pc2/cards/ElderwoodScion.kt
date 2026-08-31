package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Elderwood Scion
 * {3}{G}{W}
 * Creature — Elemental
 * 4/4
 *
 * Trample, lifelink
 * Spells you cast that target this creature cost {2} less to cast.
 * Spells your opponents cast that target this creature cost {2} more to cast.
 */
val ElderwoodScion = card("Elderwood Scion") {
    manaCost = "{3}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Elemental"
    power = 4
    toughness = 4
    oracleText = "Trample, lifelink\n" +
        "Spells you cast that target this creature cost {2} less to cast.\n" +
        "Spells your opponents cast that target this creature cost {2} more to cast."

    keywords(Keyword.TRAMPLE, Keyword.LIFELINK)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCastTargeting(GroupFilter.source()),
            modification = CostModification.ReduceGeneric(2),
        )
    }
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.OpponentsCastTargeting(GroupFilter.source()),
            modification = CostModification.IncreaseGeneric(2),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "88"
        artist = "Dan Scott"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea8855fe-6734-462f-8786-0bde324409d0.jpg?1783940617"
    }
}
