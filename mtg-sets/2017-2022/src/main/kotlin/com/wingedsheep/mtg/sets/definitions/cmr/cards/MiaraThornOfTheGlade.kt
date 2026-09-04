package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect
import com.wingedsheep.sdk.scripting.effects.PayLifeEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect

/**
 * Miara, Thorn of the Glade
 * {1}{B}
 * Legendary Creature — Elf Scout
 * 1/2
 *
 * Whenever Miara or another Elf you control dies, you may pay {1} and 1 life. If you do, draw a card.
 * Partner (You can have two commanders if both have partner.)
 *
 * "Miara or another Elf you control" is "an Elf you control" with [TriggerBinding.ANY], which fires on
 * the source's own death via last-known information. Partner is omitted; the engine does not yet model
 * multi-commander decks (see Numa, Joraga Chieftain).
 */
val MiaraThornOfTheGlade = card("Miara, Thorn of the Glade") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Elf Scout"
    power = 1
    toughness = 2
    oracleText = "Whenever Miara or another Elf you control dies, you may pay {1} and 1 life. " +
        "If you do, draw a card.\n" +
        "Partner (You can have two commanders if both have partner.)"

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.ELF).youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        effect = OptionalCostEffect(
            cost = CompositeEffect(
                listOf(
                    PayManaCostEffect(ManaCost.parse("{1}")),
                    PayLifeEffect(1),
                )
            ),
            ifPaid = Effects.DrawCards(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "133"
        artist = "Johannes Voss"
        flavorText = "\"Approach from downwind. Tread with care. Aim for the heart.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/1/41a76adc-42e1-4f65-940c-d0a5555c0633.jpg?1783928835"
    }
}
