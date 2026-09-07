package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ForetellSetupCostGating
import com.wingedsheep.sdk.scripting.ForetellSetupCostTarget
import com.wingedsheep.sdk.scripting.ModifyForetellSetupCost

/**
 * Ranar the Ever-Watchful — Kaldheim Commander (KHC) #2
 * {2}{W}{U} · Legendary Creature — Spirit Warrior · 2/3
 *
 * Flying, vigilance
 * The first card you foretell each turn costs {0} to foretell.
 * Whenever one or more cards are put into exile from your hand or a spell or ability you control
 * exiles one or more permanents from the battlefield, create a 1/1 white Spirit creature token
 * with flying.
 *
 * The foretell discount is [ModifyForetellSetupCost] with [ForetellSetupCostGating.NthForetellPerTurn]
 * (engine-tested in `ForetellSetupMechanicTest`). The exile trigger reuses
 * [Triggers.CardsPutIntoExileFromHandOrByYou] — same shape as Hero of Bretagard, but the payoff is
 * a single Spirit token per event, not a per-card counter.
 */
val RanarTheEverWatchful = card("Ranar the Ever-Watchful") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Spirit Warrior"
    power = 2
    toughness = 3
    oracleText = "Flying, vigilance\n" +
        "The first card you foretell each turn costs {0} to foretell.\n" +
        "Whenever one or more cards are put into exile from your hand or a spell or ability " +
        "you control exiles one or more permanents from the battlefield, create a 1/1 white " +
        "Spirit creature token with flying."

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    staticAbility {
        ability = ModifyForetellSetupCost(
            target = ForetellSetupCostTarget.YouForetellFromHand,
            modification = CostModification.ReduceGeneric(2),
            gating = ForetellSetupCostGating.NthForetellPerTurn(1),
        )
    }

    triggeredAbility {
        trigger = Triggers.CardsPutIntoExileFromHandOrByYou()
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/9/5/95694c03-2b3b-455c-8361-0799c078bf04.jpg?1783928083",
        )
        description = "Whenever one or more cards are put into exile from your hand or a spell or " +
            "ability you control exiles one or more permanents from the battlefield, create a 1/1 " +
            "white Spirit creature token with flying."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "2"
        artist = "Kieran Yanner"
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1c8c41dd-8551-4ce8-a9be-9b9f65718852.jpg?1783928341"
    }
}
