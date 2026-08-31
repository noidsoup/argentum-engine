package com.wingedsheep.mtg.sets.definitions.arb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.umbraArmor
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.MustBeBlockedEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Indrik Umbra
 * {4}{G}{W}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +4/+4 and has first strike, and all creatures able to block it do so.
 * Umbra armor
 */
val IndrikUmbra = card("Indrik Umbra") {
    manaCost = "{4}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +4/+4 and has first strike, and all creatures able to block it do so.\n" +
        "Umbra armor (If enchanted creature would be destroyed, instead remove all damage from it and destroy this Aura.)"

    auraTarget = Targets.Creature
    umbraArmor()

    staticAbility {
        ability = ModifyStats(+4, +4, Filters.EnchantedCreature)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE, Filters.EnchantedCreature)
    }
    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.Attacks.event,
                binding = Triggers.Attacks.binding,
                effect = MustBeBlockedEffect(EffectTarget.Self, allCreatures = true),
            ),
            filter = Filters.EnchantedCreature,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "96"
        artist = "Christopher Moeller"
        flavorText = "\"The indrik's shadow is a shield that no weapon can pierce.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc8b5e68-1938-4775-a015-bdfcb85c0593.jpg"
    }
}
