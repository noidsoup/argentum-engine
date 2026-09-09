package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Remorseful Cleric
 * {1}{W}
 * Creature — Spirit Cleric
 * 2/1
 *
 * Flying
 * Sacrifice this creature: Exile target player's graveyard.
 *
 * The graveyard exile follows the Stone of Erech / Kutzil's Flanker idiom: target a player, gather
 * their graveyard via [Player.ContextPlayer(0)], move it to exile.
 */
val RemorsefulCleric = card("Remorseful Cleric") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Cleric"
    oracleText = "Flying\nSacrifice this creature: Exile target player's graveyard."
    power = 2
    toughness = 1

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.SacrificeSelf
        target("target player", Targets.Player)
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.GRAVEYARD, Player.ContextPlayer(0)),
                    storeAs = "targetGraveyard",
                ),
                MoveCollectionEffect(
                    from = "targetGraveyard",
                    destination = CardDestination.ToZone(Zone.EXILE, Player.ContextPlayer(0)),
                ),
            ),
        )
        description = "Sacrifice this creature: Exile target player's graveyard."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "33"
        artist = "Grzegorz Rutkowski"
        flavorText = "A lifetime of keeping up appearances is not enough to earn an eternity of rest."
        imageUri = "https://cards.scryfall.io/normal/front/9/6/9620716d-9be8-4ebd-80d2-679373f4f897.jpg?1783934598"
    }
}
