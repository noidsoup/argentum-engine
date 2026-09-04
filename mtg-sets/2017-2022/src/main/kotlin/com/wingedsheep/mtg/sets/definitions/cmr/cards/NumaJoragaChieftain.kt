package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Numa, Joraga Chieftain
 * {2}{G}
 * Legendary Creature — Elf Warrior
 * 2/2
 *
 * At the beginning of combat on your turn, you may pay {X}{X}. When you do, distribute X +1/+1
 * counters among any number of target Elves.
 * Partner (You can have two commanders if both have partner.)
 *
 * The optional {X}{X} payment is modeled as [Effects.PayRepeatedly] with a {2} unit cost — paying
 * it N times costs {2N} generic mana and binds N as [DynamicAmounts.timesPaid] for the reflexive
 * distribute. Partner is omitted; the engine does not yet model multi-commander decks (see Kodama
 * of the East Tree).
 */
val NumaJoragaChieftain = card("Numa, Joraga Chieftain") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Elf Warrior"
    power = 2
    toughness = 2
    oracleText = "At the beginning of combat on your turn, you may pay {X}{X}. When you do, " +
        "distribute X +1/+1 counters among any number of target Elves.\n" +
        "Partner (You can have two commanders if both have partner.)"

    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = ReflexiveTriggerEffect(
            action = Effects.PayRepeatedly("{2}"),
            optional = true,
            reflexiveEffect = Effects.DistributeCountersAmongTargets(DynamicAmounts.timesPaid()),
            reflexiveTargetRequirements = listOf(
                TargetObject(
                    filter = TargetFilter(GameObjectFilter.Creature.withSubtype("Elf")),
                    unlimited = true,
                    dynamicMaxCount = DynamicAmounts.timesPaid(),
                ),
            ),
            descriptionOverride = "You may pay {X}{X}. When you do, distribute X +1/+1 counters " +
                "among any number of target Elves.",
        )
        description = "At the beginning of combat on your turn, you may pay {X}{X}. When you do, " +
            "distribute X +1/+1 counters among any number of target Elves."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "246"
        artist = "Kieran Yanner"
        flavorText = "His blade carves a path for his people. His words guide them along it."
        imageUri = "https://cards.scryfall.io/normal/front/9/4/9465d118-5ad5-42c5-a512-7aca5f7b905d.jpg?1783928786"
    }
}
