package com.wingedsheep.mtg.sets.definitions.ulg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rancor
 * {G}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +2/+0 and has trample.
 * When this Aura is put into a graveyard from the battlefield, return it to its owner's hand.
 *
 * The refund trigger is [Triggers.PutIntoGraveyardFromBattlefield] (any battlefield→graveyard
 * trip — destroyed, sacrificed, or falling off as an SBA), returning Self to hand.
 */
val Rancor = card("Rancor") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+0 and has trample.\n" +
        "When this Aura is put into a graveyard from the battlefield, return it to its owner's hand."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(+2, +0, Filters.EnchantedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, Filters.EnchantedCreature)
    }

    triggeredAbility {
        trigger = Triggers.PutIntoGraveyardFromBattlefield
        effect = Effects.ReturnToHand(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "110"
        artist = "Kev Walker"
        flavorText = "Hatred outlives the hateful."
        imageUri = "https://cards.scryfall.io/normal/front/5/9/59e256c2-38df-4012-9308-ce17dd889e5f.jpg?1783946229"
    }
}
