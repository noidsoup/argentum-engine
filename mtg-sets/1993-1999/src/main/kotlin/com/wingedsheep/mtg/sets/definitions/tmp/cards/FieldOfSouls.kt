package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Field of Souls
 * {2}{W}{W}
 * Enchantment
 * Whenever a nontoken creature is put into your graveyard from the battlefield, create a 1/1 white
 * Spirit creature token with flying.
 */
val FieldOfSouls = card("Field of Souls") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText =
        "Whenever a nontoken creature is put into your graveyard from the battlefield, " +
        "create a 1/1 white Spirit creature token with flying."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.nontoken().ownedByYou(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/8/3/83497714-97ae-4846-8096-f7f1524f0e09.jpg?1783924702",
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "18"
        artist = "Richard Kane Ferguson"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/9816a3ef-e2a8-4d97-afbf-d190a62265bf.jpg?1783946666"
    }
}
