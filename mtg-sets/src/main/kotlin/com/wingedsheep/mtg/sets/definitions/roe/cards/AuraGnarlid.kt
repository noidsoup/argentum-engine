package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedByCreaturesWithLessPower
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Aura Gnarlid
 * {2}{G}
 * Creature — Beast
 * 2/2
 *
 * Creatures with power less than this creature's power can't block it.
 * This creature gets +1/+1 for each Aura on the battlefield.
 */
val AuraGnarlid = card("Aura Gnarlid") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    oracleText = "Creatures with power less than this creature's power can't block it.\n" +
        "This creature gets +1/+1 for each Aura on the battlefield."
    power = 2
    toughness = 2

    staticAbility {
        ability = CantBeBlockedByCreaturesWithLessPower()
    }

    val auraCount = DynamicAmount.AggregateBattlefield(
        player = Player.Each,
        filter = GameObjectFilter.Enchantment.withSubtype(Subtype.AURA),
    )

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = auraCount,
            toughnessBonus = auraCount,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "175"
        artist = "Lars Grant-West"
        flavorText = "Kill a gnarlid with your first blow, or it'll cheerfully show you how it's done."
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f8dbb4f-4b01-4666-b62f-a2323dac7a19.jpg?1783941968"
    }
}
