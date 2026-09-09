package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.conditions.IsInStep
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Verity Circle
 * {2}{U}
 * Enchantment
 *
 * Whenever a creature an opponent controls becomes tapped, if it isn't being declared as an
 * attacker, you may draw a card.
 * {4}{U}: Tap target creature without flying.
 */
val VerityCircle = card("Verity Circle") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature an opponent controls becomes tapped, if it isn't being " +
        "declared as an attacker, you may draw a card.\n" +
        "{4}{U}: Tap target creature without flying."

    triggeredAbility {
        trigger = Triggers.becomesTapped(
            binding = TriggerBinding.ANY,
            filter = GameObjectFilter.Creature.opponentControls(),
        )
        interveningIf = Conditions.Not(
            Conditions.All(
                IsInStep(listOf(Step.DECLARE_ATTACKERS), yoursOnly = false),
                Conditions.EntityMatches(
                    EffectTarget.TriggeringEntity,
                    GameObjectFilter.Any.attackedThisCombat(),
                ),
            ),
        )
        effect = MayEffect(Effects.DrawCards(1))
        description = "Whenever a creature an opponent controls becomes tapped, if it isn't " +
            "being declared as an attacker, you may draw a card."
    }

    activatedAbility {
        cost = Costs.Mana("{4}{U}")
        val creature = target(
            "target creature without flying",
            TargetPermanent(filter = TargetFilter.Creature.withoutKeyword(Keyword.FLYING)),
        )
        effect = Effects.Tap(creature)
        description = "Tap target creature without flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "58"
        artist = "Volkan Ba\u01f5a"
        flavorText = "\"Here, there is only truth.\"\n\u2014Barvisa, Azorius emissary"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d866d26-b630-46d3-bcc2-b810c844cc89.jpg?1783933701"
    }
}
