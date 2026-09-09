package com.wingedsheep.mtg.sets.definitions.c18.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.EmitLibrarySearchedEventEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Boreas Charger
 * {2}{W}
 * Creature — Pegasus
 * 2/1
 *
 * Flying
 * When this creature leaves the battlefield, choose an opponent who controls more lands than you.
 * Search your library for a number of Plains cards equal to the difference, reveal those cards,
 * put one of them onto the battlefield tapped and the rest into your hand, then shuffle.
 *
 * The land difference is [DynamicAmount.IfPositive] over opponent-minus-you land counts, keyed to
 * [Player.ChosenOpponent] after [Effects.ChooseOpponent]. The Cultivate split (one tapped, rest
 * to hand) is the Troop of Ponies / Bloomvine Regent pipeline, not a single searchLibrary call.
 */
private val chosenOpponentLandCount =
    DynamicAmount.Count(Player.ChosenOpponent, Zone.BATTLEFIELD, GameObjectFilter.Land)

private val yourLandCount =
    DynamicAmount.Count(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land)

private val plainsToFind = DynamicAmount.IfPositive(
    DynamicAmount.Subtract(chosenOpponentLandCount, yourLandCount),
)

private val chosenOpponentControlsMoreLands = Conditions.CompareAmounts(
    left = chosenOpponentLandCount,
    operator = ComparisonOperator.GT,
    right = yourLandCount,
)

val BoreasCharger = card("Boreas Charger") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Pegasus"
    oracleText = "Flying\n" +
        "When this creature leaves the battlefield, choose an opponent who controls more lands than " +
        "you. Search your library for a number of Plains cards equal to the difference, reveal " +
        "those cards, put one of them onto the battlefield tapped and the rest into your hand, " +
        "then shuffle."
    power = 2
    toughness = 1

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.Composite(
            listOf(
                Effects.ChooseOpponent("Choose an opponent who controls more lands than you"),
                ConditionalEffect(
                    condition = chosenOpponentControlsMoreLands,
                    effect = Effects.Composite(
                        listOf(
                            GatherCardsEffect(
                                source = CardSource.FromZone(
                                    Zone.LIBRARY,
                                    Player.You,
                                    GameObjectFilter.Land.withSubtype(Subtype.PLAINS),
                                ),
                                storeAs = "searchable",
                            ),
                            SelectFromCollectionEffect(
                                from = "searchable",
                                selection = SelectionMode.ChooseUpTo(plainsToFind),
                                storeSelected = "found",
                                prompt = "Search your library for Plains cards equal to the land difference",
                            ),
                            SelectFromCollectionEffect(
                                from = "found",
                                selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                                storeSelected = "toBattlefield",
                                storeRemainder = "toHand",
                                selectedLabel = "Onto the battlefield tapped",
                                remainderLabel = "Into your hand",
                                prompt = "Choose which Plains enters the battlefield tapped; the rest go to your hand.",
                            ),
                            MoveCollectionEffect(
                                from = "toBattlefield",
                                destination = CardDestination.ToZone(
                                    Zone.BATTLEFIELD,
                                    placement = ZonePlacement.Tapped,
                                ),
                                revealed = true,
                            ),
                            MoveCollectionEffect(
                                from = "toHand",
                                destination = CardDestination.ToZone(Zone.HAND),
                                revealed = true,
                            ),
                            ShuffleLibraryEffect(),
                            EmitLibrarySearchedEventEffect,
                        ),
                    ),
                ),
            ),
        )
        description = "When this creature leaves the battlefield, choose an opponent who controls " +
            "more lands than you. Search your library for a number of Plains cards equal to the " +
            "difference, reveal those cards, put one of them onto the battlefield tapped and the " +
            "rest into your hand, then shuffle."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "1"
        artist = "Christine Choi"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5cd3b85b-7d8d-402e-829a-6080fd4eb7a1.jpg?1783934345"
    }
}
