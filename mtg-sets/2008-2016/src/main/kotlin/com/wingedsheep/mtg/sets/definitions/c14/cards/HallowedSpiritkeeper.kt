package com.wingedsheep.mtg.sets.definitions.c14.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hallowed Spiritkeeper
 * {1}{W}{W}
 * Creature — Avatar
 * 3/2
 *
 * Vigilance
 * When this creature dies, create X 1/1 white Spirit creature tokens with flying, where X is the
 * number of creature cards in your graveyard.
 *
 * The token count is [DynamicAmounts.creatureCardsInYourGraveyard] at resolution time — after this
 * creature has reached the graveyard, so it counts itself.
 */
val HallowedSpiritkeeper = card("Hallowed Spiritkeeper") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Avatar"
    oracleText = "Vigilance\n" +
        "When this creature dies, create X 1/1 white Spirit creature tokens with flying, where X " +
        "is the number of creature cards in your graveyard."
    power = 3
    toughness = 2

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            count = DynamicAmounts.creatureCardsInYourGraveyard(),
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/8/3/83497714-97ae-4846-8096-f7f1524f0e09.jpg?1783924702",
        )
        description = "When this creature dies, create X 1/1 white Spirit creature tokens with " +
            "flying, where X is the number of creature cards in your graveyard."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "8"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94ebd5bd-99b3-4371-9ca2-e0504db1458e.jpg?1783938873"
    }
}
