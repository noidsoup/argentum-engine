package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ethereal Investigator
 * {3}{U}
 * Creature — Spirit
 * 2/3
 *
 * Flying
 * When this creature enters, investigate X times, where X is the number of opponents you have.
 * Whenever you draw your second card each turn, create a 1/1 white Spirit creature token with flying.
 *
 * The ETB investigate count is [DynamicAmount.PlayerCount] over [Player.EachOpponent] — the same
 * shape as Inspired Sphinx's draw-on-ETB. The draw payoff is [Triggers.NthCardDrawn]`(2)` (CR
 * 121.2): one two-card draw fires it once, and the Clue from investigate counts toward the tally.
 */
val EtherealInvestigator = card("Ethereal Investigator") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    oracleText = "Flying\n" +
        "When this creature enters, investigate X times, where X is the number of opponents you " +
        "have. (To investigate, create a Clue token. It's an artifact with \"{2}, Sacrifice this " +
        "token: Draw a card.\")\n" +
        "Whenever you draw your second card each turn, create a 1/1 white Spirit creature token " +
        "with flying."
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Investigate(DynamicAmount.PlayerCount(Player.EachOpponent))
        description = "When this creature enters, investigate X times, where X is the number of " +
            "opponents you have."
    }

    triggeredAbility {
        trigger = Triggers.NthCardDrawn(2)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/8/3/83497714-97ae-4846-8096-f7f1524f0e09.jpg?1783924702",
        )
        description = "Whenever you draw your second card each turn, create a 1/1 white Spirit " +
            "creature token with flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "12"
        artist = "Sean Murray"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a81b1492-ef70-4d8d-9458-c25a084107a7.jpg?1783925004"
    }
}
