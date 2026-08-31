package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.SelectionRestriction
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Three Dreams
 * {4}{W}
 * Sorcery
 *
 * Search your library for up to three Aura cards with different names, reveal them, put them into
 * your hand, then shuffle.
 */
val ThreeDreams = card("Three Dreams") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Search your library for up to three Aura cards with different names, reveal them, " +
        "put them into your hand, then shuffle."

    spell {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        Zone.LIBRARY,
                        Player.You,
                        GameObjectFilter.Enchantment.withSubtype("Aura"),
                    ),
                    storeAs = "auras",
                ),
                SelectFromCollectionEffect(
                    from = "auras",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(3)),
                    storeSelected = "found",
                    restrictions = listOf(SelectionRestriction.OnePerCardName),
                    prompt = "Search for up to three Aura cards with different names",
                ),
                MoveCollectionEffect(
                    from = "found",
                    destination = CardDestination.ToZone(Zone.HAND),
                    revealed = true,
                ),
                ShuffleLibraryEffect(),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "32"
        artist = "Shishizaru"
        flavorText = "\"Choose one to heal, one to harm, and one to grant you the prudence to use them.\"\n" +
            "—Miotri, auratouched mage"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1fb144b8-f2ee-4d35-814d-ceb728b2ab75.jpg?1783942533"
    }
}
