package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Bounty of Skemfar — Kaldheim Commander (KHC) #12
 * {2}{G} · Sorcery
 *
 * Reveal the top six cards of your library. You may put up to one land card from among them onto
 * the battlefield tapped and up to one Elf card from among them into your hand. Put the rest on
 * the bottom of your library in a random order.
 *
 * Two independent optional picks over one public reveal, then route each pile — the Bounty shape
 * Assay calls out as distinct from [com.wingedsheep.sdk.dsl.Patterns.Library.lookAtTopAndTakeMatching].
 */
val BountyOfSkemfar = card("Bounty of Skemfar") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Reveal the top six cards of your library. You may put up to one land card from " +
        "among them onto the battlefield tapped and up to one Elf card from among them into your " +
        "hand. Put the rest on the bottom of your library in a random order."

    spell {
        effect = Effects.Pipeline {
            val revealed = gather(
                CardSource.TopOfLibrary(DynamicAmount.Fixed(6)),
                revealed = true,
                name = "revealed",
            )
            reveal(revealed)
            val landPick = chooseUpTo(
                count = 1,
                from = revealed,
                filter = Filters.Land,
                prompt = "You may put up to one land card from among them onto the battlefield tapped",
                showAllCards = true,
                name = "landPick",
            )
            val elfPick = chooseUpTo(
                count = 1,
                from = revealed,
                filter = GameObjectFilter.Any.withSubtype(Subtype.ELF),
                prompt = "You may put up to one Elf card from among them into your hand",
                showAllCards = true,
                name = "elfPick",
            )
            move(
                from = landPick,
                destination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = ZonePlacement.Tapped),
                revealed = true,
            )
            move(
                from = elfPick,
                destination = CardDestination.ToZone(Zone.HAND),
                revealed = true,
            )
            val withoutLand = exclude(revealed, landPick, name = "withoutLand")
            val rest = exclude(withoutLand, elfPick, name = "rest")
            move(
                from = rest,
                destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                order = CardOrder.Random,
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "12"
        artist = "Colin Boyer"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/3185a67e-648c-48a8-9aad-180e1ca0f4ae.jpg?1783928336"
    }
}
