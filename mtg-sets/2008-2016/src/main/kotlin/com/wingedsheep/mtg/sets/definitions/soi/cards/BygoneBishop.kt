package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.SpellCastEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Bygone Bishop
 * {2}{W}
 * Creature — Spirit Cleric
 * 2/3
 *
 * Flying
 * Whenever you cast a creature spell with mana value 3 or less, investigate.
 */
val BygoneBishop = card("Bygone Bishop") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Cleric"
    power = 2
    toughness = 3
    oracleText = "Flying\n" +
        "Whenever you cast a creature spell with mana value 3 or less, investigate. " +
        "(Create a Clue token. It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")"

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = TriggerSpec(
            SpellCastEvent(
                spellFilter = GameObjectFilter.Creature.manaValueAtMost(3),
                player = Player.You,
            ),
            TriggerBinding.ANY,
        )
        effect = Effects.Investigate()
        description = "Whenever you cast a creature spell with mana value 3 or less, investigate."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "8"
        artist = "Jason A. Engle"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c45198f-fb2b-4c83-abdc-8b1a0071cfed.jpg?1783937823"
    }
}
