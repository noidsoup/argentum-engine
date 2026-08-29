package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Quietus Spike
 * {3}
 * Artifact — Equipment
 *
 * Equipped creature has deathtouch.
 * Whenever equipped creature deals combat damage to a player, that player loses half their life,
 * rounded up.
 * Equip {3}
 */
val QuietusSpike = card("Quietus Spike") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has deathtouch.\n" +
        "Whenever equipped creature deals combat damage to a player, that player loses half their " +
        "life, rounded up.\n" +
        "Equip {3}"

    staticAbility {
        ability = GrantKeyword(Keyword.DEATHTOUCH, Filters.EquippedCreature)
    }

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            binding = TriggerBinding.ATTACHED,
        )
        effect = Effects.LoseHalfLife(
            roundUp = true,
            target = EffectTarget.PlayerRef(Player.DefendingPlayer),
            lifePlayer = Player.DefendingPlayer,
        )
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "217"
        artist = "Mark Brill"
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e90d1b27-fb90-4649-a905-f8e90d4a1852.jpg?1783942533"
    }
}
