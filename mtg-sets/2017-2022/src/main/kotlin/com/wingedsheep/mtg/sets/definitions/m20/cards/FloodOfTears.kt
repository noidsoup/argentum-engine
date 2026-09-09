package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination

/**
 * Flood of Tears
 * {4}{U}{U}
 * Sorcery
 *
 * Return all nonland permanents to their owners' hands. If you return four or more nontoken
 * permanents you control this way, you may put a permanent card from your hand onto the battlefield.
 *
 * The payoff counts nontoken permanents you controlled before the mass bounce (the Faerie Slumber
 * Party snapshot shape): filter the gathered nonlands while they are still on the battlefield, bounce
 * everything, then gate [Patterns.Hand.putFromHand] on that pre-move collection holding at least four.
 */
val FloodOfTears = card("Flood of Tears") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Return all nonland permanents to their owners' hands. If you return four or more " +
        "nontoken permanents you control this way, you may put a permanent card from your hand onto " +
        "the battlefield."

    spell {
        effect = Effects.Pipeline {
            val nonlands = gather(
                GameObjectFilter.NonlandPermanent,
                name = "floodNonlands",
            )
            val yoursNontoken = filter(
                nonlands,
                GameObjectFilter.NonlandPermanent.nontoken().youControl(),
                name = "floodYoursNontoken",
            )
            move(nonlands, CardDestination.ToZone(Zone.HAND))
            ifNotEmpty(yoursNontoken, minSize = 4) {
                run(Patterns.Hand.putFromHand(filter = GameObjectFilter.Permanent))
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "59"
        artist = "Adam Paquette"
        flavorText = "\"I have wept such a torrent as to scour the land clean.\"\n—Mu Yanling"
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c3621820-4ab5-42bf-8a02-d5c066db4653.jpg?1783933010"
    }
}
