package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.EmitLibrarySearchedEventEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Stoic Farmer — Kaldheim Commander (KHC) #5
 * {3}{W} · Creature — Dwarf Peasant · 3/3
 *
 * When this creature enters, search your library for a basic Plains card and reveal it. If an
 * opponent controls more lands than you, put it onto the battlefield tapped. Otherwise, put it
 * into your hand. Then shuffle.
 * Foretell {1}{W}
 *
 * The ETB search cannot use `Patterns.Library.searchLibrary` (one fixed destination): the branch
 * is on [Conditions.OpponentControlsMoreLands], read at resolution after the reveal (Claim Jumper's
 * land-count comparison, Guidelight Pathmaker's gather → reveal → branch shape). Both branches
 * move the same `found` collection; declining is impossible because the search is mandatory, but
 * an empty library leaves `found` empty and both moves no-op while the shuffle still runs.
 *
 * Foretell is display-only as `Keyword.FORETELL`; cast/exile wiring is
 * [KeywordAbility.foretell] (Tales of the Ancestors / Demon Bolt).
 */
val StoicFarmer = card("Stoic Farmer") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Peasant"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, search your library for a basic Plains card and " +
        "reveal it. If an opponent controls more lands than you, put it onto the battlefield " +
        "tapped. Otherwise, put it into your hand. Then shuffle.\n" +
        "Foretell {1}{W} (During your turn, you may pay {2} and exile this card from your hand " +
        "face down. Cast it on a later turn for its foretell cost.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        Zone.LIBRARY,
                        Player.You,
                        GameObjectFilter.BasicLand.withSubtype(Subtype.PLAINS),
                    ),
                    storeAs = "searchable",
                ),
                SelectFromCollectionEffect(
                    from = "searchable",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "found",
                ),
                RevealCollectionEffect(from = "found"),
                ConditionalEffect(
                    condition = Conditions.OpponentControlsMoreLands,
                    effect = MoveCollectionEffect(
                        from = "found",
                        destination = CardDestination.ToZone(
                            Zone.BATTLEFIELD,
                            placement = ZonePlacement.Tapped,
                        ),
                        revealed = true,
                    ),
                ),
                ConditionalEffect(
                    condition = Conditions.Not(Conditions.OpponentControlsMoreLands),
                    effect = MoveCollectionEffect(
                        from = "found",
                        destination = CardDestination.ToZone(Zone.HAND),
                        revealed = true,
                    ),
                ),
                ShuffleLibraryEffect(),
                EmitLibrarySearchedEventEffect,
            ),
        )
        description = "When this creature enters, search your library for a basic Plains card and " +
            "reveal it. If an opponent controls more lands than you, put it onto the battlefield " +
            "tapped. Otherwise, put it into your hand. Then shuffle."
    }

    keywordAbility(KeywordAbility.foretell("{1}{W}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "5"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f53f39e9-f07e-444f-8420-4545e56253e5.jpg?1783928340"
    }
}
