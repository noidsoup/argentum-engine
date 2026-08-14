package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Relentless Skaabs
 * {3}{U}{U}
 * Creature — Zombie
 * 4/4
 * As an additional cost to cast this spell, exile a creature card from your graveyard.
 * Undying (When this creature dies, if it had no +1/+1 counters on it, return it to the battlefield
 * under its owner's control with a +1/+1 counter on it.)
 */
val RelentlessSkaabs = card("Relentless Skaabs") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie"
    oracleText =
        "As an additional cost to cast this spell, exile a creature card from your graveyard.\n" +
            "Undying (When this creature dies, if it had no +1/+1 counters on it, return it to the " +
            "battlefield under its owner's control with a +1/+1 counter on it.)"
    power = 4
    toughness = 4

    additionalCost(Costs.additional.ExileCards(count = 1, filter = GameObjectFilter.Creature))
    keywords(Keyword.UNDYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "45"
        artist = "Karl Kopinski"
        imageUri =
            "https://cards.scryfall.io/normal/front/b/3/b3304cab-0dc9-47e4-ac68-00974b64f5a0.jpg?1783940838"
    }
}
