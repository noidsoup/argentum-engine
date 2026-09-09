package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Haunted Library
 * {1}{W}
 * Enchantment
 *
 * Whenever a creature an opponent controls dies, you may pay {1}. If you do, create a 1/1 white
 * Spirit creature token with flying.
 */
val HauntedLibrary = card("Haunted Library") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature an opponent controls dies, you may pay {1}. If you do, " +
        "create a 1/1 white Spirit creature token with flying."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.opponentControls(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{1}"),
            effect = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Spirit"),
                keywords = setOf(Keyword.FLYING),
                imageUri = "https://cards.scryfall.io/normal/front/8/3/83497714-97ae-4846-8096-f7f1524f0e09.jpg?1783924702",
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "6"
        artist = "Justyna Dura"
        flavorText = "The library of Moormanor had been open to all before the Travails, and it " +
            "continues to attract curious visitors."
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0193151-0f5f-457b-a6b2-08f66b53c9b2.jpg?1783925008"
    }
}
