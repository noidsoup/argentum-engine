package com.wingedsheep.mtg.sets.definitions.sok.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kami of the Crescent Moon
 * {U}{U}
 * Legendary Creature — Spirit
 * 1/3
 *
 * At the beginning of each player's draw step, that player draws an additional card.
 *
 * Same each-player draw-step trigger shape as Dictate of Kruphix — [Player.TriggeringPlayer]
 * resolves to the active player of that draw step, matching "that player".
 *
 * Canonical earliest printing: Saviors of Kamigawa (SOK). VOC and other sets are reprints.
 */
val KamiOfTheCrescentMoon = card("Kami of the Crescent Moon") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Spirit"
    oracleText = "At the beginning of each player's draw step, that player draws an additional card."
    power = 1
    toughness = 3

    triggeredAbility {
        trigger = Triggers.phase(Step.DRAW, Player.Each)
        effect = Effects.DrawCards(1, EffectTarget.PlayerRef(Player.TriggeringPlayer))
        description = "At the beginning of each player's draw step, that player draws an additional card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "42"
        artist = "Darrell Riche"
        flavorText = "\"He's a lot like me, that masterless little kami . . . unimpressed by grandeur " +
            "and never at a loss for a trick.\"\n—Toshiro Umezawa"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f01ee008-76dd-4d4d-8273-ad5b28c8a2c7.jpg?1783944163"
    }
}
