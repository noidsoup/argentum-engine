package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.umbraArmor
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Snake Umbra
 * {2}{G}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +1/+1 and has "Whenever this creature deals damage to an opponent,
 * you may draw a card."
 * Umbra armor
 */
val SnakeUmbra = card("Snake Umbra") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+1 and has \"Whenever this creature deals damage to an opponent, you may draw a card.\"\n" +
        "Umbra armor (If enchanted creature would be destroyed, instead remove all damage from it and destroy this Aura.)"

    auraTarget = Targets.Creature
    umbraArmor()

    staticAbility {
        ability = ModifyStats(+1, +1, Filters.EnchantedCreature)
    }

    val drawOnDamageToOpponent = TriggeredAbility.create(
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Any,
            recipient = RecipientFilter.Opponent,
        ).event,
        binding = Triggers.dealsDamage(
            damageType = DamageType.Any,
            recipient = RecipientFilter.Opponent,
        ).binding,
        effect = MayEffect(Effects.DrawCards(1)),
    )

    staticAbility {
        ability = GrantTriggeredAbility(drawOnDamageToOpponent, Filters.EnchantedCreature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "207"
        artist = "Christopher Moeller"
        flavorText = "\"The snake's venom is a gift to the strong. It has no sting for the meek.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a011820-80c6-484f-84be-f07f0e45f1a8.jpg"
    }
}
