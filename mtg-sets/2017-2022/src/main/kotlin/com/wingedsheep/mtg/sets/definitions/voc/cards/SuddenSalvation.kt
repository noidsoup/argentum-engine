package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sudden Salvation
 * {2}{W}{W}
 * Instant
 *
 * Choose up to three target permanent cards in graveyards that were put there from the battlefield
 * this turn. Return them to the battlefield tapped under their owners' control. You draw a card for
 * each opponent who controls one or more of those permanents.
 *
 * Gather → move (tapped, under owners' control, `storeMovedAs`) → draw per opponent controlling a
 * returned permanent. `putIntoGraveyardFromBattlefieldThisTurn()` is the whole "from the battlefield
 * this turn" clause; `underOwnersControl` is the owners'-control half.
 */
val SuddenSalvation = card("Sudden Salvation") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Choose up to three target permanent cards in graveyards that were put there from " +
        "the battlefield this turn. Return them to the battlefield tapped under their owners' " +
        "control. You draw a card for each opponent who controls one or more of those permanents."

    spell {
        target(
            "up to three target permanent cards in graveyards that were put there from the battlefield this turn",
            TargetObject(
                optional = true,
                count = 3,
                filter = TargetFilter(
                    GameObjectFilter.Permanent.putIntoGraveyardFromBattlefieldThisTurn(),
                    zone = Zone.GRAVEYARD,
                ),
            ),
        )
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(source = CardSource.ChosenTargets, storeAs = "returned"),
                MoveCollectionEffect(
                    from = "returned",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = ZonePlacement.Tapped),
                    underOwnersControl = true,
                    storeMovedAs = "returned",
                ),
                Effects.DrawCardsForEachOpponentControllingFromCollection("returned"),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "10"
        artist = "Cristi Balanescu"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/ded1c959-454c-4336-8eb5-0160c49d895d.jpg?1783925004"
    }
}
