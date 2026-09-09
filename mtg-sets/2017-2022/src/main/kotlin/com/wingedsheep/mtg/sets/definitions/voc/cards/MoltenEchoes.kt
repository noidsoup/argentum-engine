package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Molten Echoes
 * {2}{R}{R}
 * Enchantment
 *
 * As this enchantment enters, choose a creature type.
 * Whenever a nontoken creature you control of the chosen type enters, create a token that's a copy
 * of that creature. That token gains haste. Exile it at the beginning of the next end step.
 *
 * [EntersWithChoice] stores the creature type; the ETB trigger filters with
 * `Creature.youControl().nontoken().withChosenSubtype()` so token copies do not re-trigger.
 * `exileAtStep = Step.END` is Heat Shimmer's "beginning of the next end step" exile rider.
 */
val MoltenEchoes = card("Molten Echoes") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText =
        "As this enchantment enters, choose a creature type.\n" +
            "Whenever a nontoken creature you control of the chosen type enters, create a token " +
            "that's a copy of that creature. That token gains haste. Exile it at the beginning " +
            "of the next end step."

    replacementEffect(EntersWithChoice(ChoiceType.CREATURE_TYPE))

    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(
                filter = GameObjectFilter.Creature.youControl().nontoken().withChosenSubtype(),
                to = Zone.BATTLEFIELD,
            ),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.CreateTokenCopyOfTarget(
            EffectTarget.TriggeringEntity,
            addedKeywords = setOf(Keyword.HASTE),
            exileAtStep = Step.END,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "148"
        artist = "Zoltan Boros"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/461d9f1c-53a4-48a6-bafb-0e70099f276c.jpg?1783924947"
    }
}
