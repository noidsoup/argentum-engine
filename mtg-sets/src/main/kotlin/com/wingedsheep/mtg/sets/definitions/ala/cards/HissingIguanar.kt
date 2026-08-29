package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hissing Iguanar
 * {2}{R}
 * Creature — Lizard
 * 3/1
 *
 * Whenever another creature dies, you may have this creature deal 1 damage to target player or
 * planeswalker.
 */
val HissingIguanar = card("Hissing Iguanar") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard"
    oracleText = "Whenever another creature dies, you may have this creature deal 1 damage to " +
        "target player or planeswalker."
    power = 3
    toughness = 1

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature,
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER,
        )
        val target = target("target player or planeswalker", Targets.PlayerOrPlaneswalker)
        effect = MayEffect(DealDamageEffect(1, target, damageSource = EffectTarget.Self))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "104"
        artist = "Brandon Kitkouski"
        flavorText = "Viashino thrashes keep iguanars as hunting companions, giving them wounded " +
            "captives as playthings."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b8b8b90-cb6e-4910-bc40-d96b78b0d70c.jpg?1783942560"
    }
}
