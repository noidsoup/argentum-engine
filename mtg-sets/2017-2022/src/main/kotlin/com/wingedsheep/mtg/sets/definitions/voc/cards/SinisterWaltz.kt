package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sinister Waltz — Innistrad: Crimson Vow Commander (VOC) #30
 * {3}{B}{R} · Sorcery
 *
 * Choose three target creature cards in your graveyard. Return two of them at random to the
 * battlefield and put the other on the bottom of your library.
 *
 * The three graveyard creatures are locked in as spell targets at cast time
 * ([CardSource.ChosenTargets]). Resolution randomly picks two for the battlefield
 * ([PipelineBuilder.chooseRandom]) and bottom-decks the remainder (Michelangelo's Technique /
 * Footbottom Feast pipeline shape).
 */
val SinisterWaltz = card("Sinister Waltz") {
    manaCost = "{3}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Sorcery"
    oracleText = "Choose three target creature cards in your graveyard. Return two of them at " +
        "random to the battlefield and put the other on the bottom of your library."

    spell {
        target(
            "three target creature cards in your graveyard",
            TargetObject(count = 3, filter = TargetFilter.CreatureInYourGraveyard),
        )
        effect = Effects.Pipeline {
            val chosen = gather(CardSource.ChosenTargets, name = "waltzChosen")
            val returned = chooseRandom(2, from = chosen, name = "waltzReturned")
            val remainder = exclude(from = chosen, minus = returned, name = "waltzRemainder")
            move(returned, CardDestination.ToZone(Zone.BATTLEFIELD, Player.You))
            move(
                remainder,
                CardDestination.ToZone(Zone.LIBRARY, Player.You, ZonePlacement.Bottom),
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "30"
        artist = "Jason Rainville"
        flavorText = "For one evening, the dueling bloodlines channeled their feuds into elaborate footwork."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e44f81b2-768f-4874-99e3-2f87b73b0159.jpg?1783924997"
    }
}
