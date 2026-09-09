package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GrantActivatedAbility

/**
 * Underworld Connections
 * {1}{B}{B}
 * Enchantment — Aura
 *
 * Enchant land
 * Enchanted land has "{T}, Pay 1 life: Draw a card."
 *
 * Canonical printing: Return to Ravnica (RTR), the card's earliest real expansion printing.
 * The granted ability is a separate activated ability on the land (not a mana ability), modeled
 * as [GrantActivatedAbility] over the enchanted land — the same shape as New Horizons / Abundant
 * Growth, with a composite cost of tap plus pay 1 life.
 */
val UnderworldConnections = card("Underworld Connections") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant land\n" +
        "Enchanted land has \"{T}, Pay 1 life: Draw a card.\""

    auraTarget = Targets.Land

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(Costs.Tap, Costs.PayLife(1)),
                effect = Effects.DrawCards(1),
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "83"
        artist = "Yeong-Hao Han"
        imageUri = "https://cards.scryfall.io/normal/front/1/9/19c52e3b-b3b8-4243-96fe-fa4c8eea7c59.jpg?1783940358"
    }
}
