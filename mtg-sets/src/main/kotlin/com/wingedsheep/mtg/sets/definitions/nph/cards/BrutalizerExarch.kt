package com.wingedsheep.mtg.sets.definitions.nph.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Brutalizer Exarch
 * {5}{G}
 * Creature — Phyrexian Cleric
 * 3/3
 *
 * When this creature enters, choose one —
 * • Search your library for a creature card, reveal it, then shuffle and put that card on top.
 * • Put target noncreature permanent on the bottom of its owner's library.
 */
val BrutalizerExarch = card("Brutalizer Exarch") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Phyrexian Cleric"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, choose one —\n" +
        "• Search your library for a creature card, reveal it, then shuffle and put that card on top.\n" +
        "• Put target noncreature permanent on the bottom of its owner's library."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                Patterns.Library.searchLibrary(
                    filter = GameObjectFilter.Creature,
                    destination = SearchDestination.TOP_OF_LIBRARY,
                    reveal = true,
                ),
                "Search your library for a creature card, reveal it, then shuffle and put that card on top.",
            ),
            Mode.withTarget(
                Effects.PutOnBottomOfLibrary(EffectTarget.ContextTarget(0)),
                TargetPermanent(filter = TargetFilter.NoncreaturePermanent),
                "Put target noncreature permanent on the bottom of its owner's library.",
            ),
        )
        description = "When this creature enters, choose one."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "105"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9ddfa4ed-70fb-4e25-875d-df0f973f7294.jpg?1783941304"
    }
}
