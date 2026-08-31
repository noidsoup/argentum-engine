package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * See Beyond
 * {1}{U}
 * Sorcery
 *
 * Draw two cards, then shuffle a card from your hand into your library.
 */
val SeeBeyond = card("See Beyond") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Draw two cards, then shuffle a card from your hand into your library."

    spell {
        effect = Effects.DrawCards(2).then(
            Effects.Composite(
                listOf(
                    GatherCardsEffect(
                        source = CardSource.FromZone(Zone.HAND, Player.You),
                        storeAs = "hand",
                    ),
                    SelectFromCollectionEffect(
                        from = "hand",
                        selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                        chooser = Chooser.Controller,
                        storeSelected = "shuffled",
                        prompt = "Choose a card to shuffle into your library",
                    ),
                    MoveCollectionEffect(
                        from = "shuffled",
                        destination = CardDestination.ToZone(
                            Zone.LIBRARY,
                            Player.You,
                            ZonePlacement.Shuffled,
                        ),
                    ),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Andrew Robinson"
        flavorText = "Ancient lore locked in a mind driven mad is just as safe as when it was locked deep underground."
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a7242e6e-b2e6-42f5-b611-3e8e0dfc0b6b.jpg?1783941991"
    }
}
