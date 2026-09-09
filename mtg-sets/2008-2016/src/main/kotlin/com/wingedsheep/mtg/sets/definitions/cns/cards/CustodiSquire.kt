package com.wingedsheep.mtg.sets.definitions.cns.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.COUNCIL_WINNERS
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Custodi Squire — Conspiracy (CNS) #18
 * {4}{W} · Creature — Spirit Cleric · 3/3
 *
 * Flying
 * Will of the council — When this creature enters, starting with you, each player votes for an
 * artifact, creature, or enchantment card in your graveyard. Return each card with the most votes
 * or tied for most votes to your hand.
 */
val CustodiSquire = card("Custodi Squire") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Cleric"
    power = 3
    toughness = 3
    oracleText = "Flying\nWill of the council — When this creature enters, starting with you, " +
        "each player votes for an artifact, creature, or enchantment card in your graveyard. " +
        "Return each card with the most votes or tied for most votes to your hand."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.council(
            vote = Patterns.Hand.playerChoiceFromGraveyard(
                owner = Player.You,
                filter = GameObjectFilter.ArtifactCreatureOrEnchantment,
            ),
            payoff = MoveCollectionEffect(
                from = COUNCIL_WINNERS,
                destination = CardDestination.ToZone(Zone.HAND, Player.You),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Alex Horley-Orlandelli"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9151422-8df1-409c-a686-0cd89247eb43.jpg?1783939377"
    }
}
