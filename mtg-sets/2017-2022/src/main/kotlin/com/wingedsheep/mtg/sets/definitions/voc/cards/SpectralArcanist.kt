package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AfterResolveDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Spectral Arcanist
 * {3}{U}
 * Creature — Spirit Wizard
 * 3/2
 *
 * Flying
 * When this creature enters, you may cast an instant or sorcery spell with mana value less than
 * or equal to the number of Spirits you control from a graveyard without paying its mana cost.
 * If that spell would be put into a graveyard, exile it instead.
 */
val SpectralArcanist = card("Spectral Arcanist") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit Wizard"
    oracleText = "Flying\n" +
        "When this creature enters, you may cast an instant or sorcery spell with mana value less " +
        "than or equal to the number of Spirits you control from a graveyard without paying its " +
        "mana cost. If that spell would be put into a graveyard, exile it instead."
    power = 3
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromZone(
                    zone = Zone.GRAVEYARD,
                    player = Player.Each,
                    filter = GameObjectFilter.InstantOrSorcery.manaValueAtMostDynamic(
                        DynamicAmounts.battlefield(
                            Player.You,
                            GameObjectFilter.Permanent.withSubtype(Subtype.SPIRIT),
                        ).count(),
                    ),
                ),
                storeAs = "eligibleSpells",
            ),
            SelectFromCollectionEffect(
                from = "eligibleSpells",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                storeSelected = "toCast",
                prompt = "You may cast an instant or sorcery spell from a graveyard without paying its mana cost",
            ),
            Effects.CastFromCollectionWithoutPayingCost(
                from = "toCast",
                insteadOfGraveyard = AfterResolveDestination.EXILE,
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "15"
        artist = "Johan Grenier"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f65bb20b-e207-49da-9d15-a8718b53783e.jpg?1783925003"
    }
}
