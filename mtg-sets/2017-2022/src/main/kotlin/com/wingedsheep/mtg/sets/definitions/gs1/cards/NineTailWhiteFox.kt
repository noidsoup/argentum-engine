package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nine-Tail White Fox — Global Series: Jiang Yanggu & Mu Yanling #8
 * {2}{U} · Creature — Fox Spirit · 2/2
 *
 * Whenever this creature deals combat damage to a player, draw a card.
 */
val NineTailWhiteFox = card("Nine-Tail White Fox") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Fox Spirit"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature deals combat damage to a player, draw a card."

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "林玄泰"
        flavorText =
            "It has waited a millennium just for you, and for a millennium will it watch over your descendants."
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a120de1b-cfc2-4a21-9eb0-ddabb3d90896.jpg?1783934633"
    }
}
