package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sai of the Shinobi
 * {1}
 * Artifact — Equipment
 *
 * Equipped creature gets +1/+1.
 * Whenever a creature you control enters, you may attach this Equipment to it.
 * Equip {2}
 */
val SaiOfTheShinobi = card("Sai of the Shinobi") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+1.\n" +
        "Whenever a creature you control enters, you may attach this Equipment to it.\n" +
        "Equip {2}"

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY,
        )
        optional = true
        effect = Effects.AttachEquipment(EffectTarget.TriggeringEntity)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "113"
        artist = "Brian Snõddy"
        flavorText = "The passing of the sai presages the end of the old clan and the ascent of the new."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f11deb8-aca1-4cdf-b443-2aecb054fb10.jpg?1783940589"
    }
}
