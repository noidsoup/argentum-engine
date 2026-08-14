package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Predatory Urge
 * {3}{G}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has "{T}: This creature deals damage equal to its power to target creature.
 * That creature deals damage equal to its power to this creature."
 *
 * Deliberately **not** modeled as a fight. The printed wording is two sequential damage clauses,
 * and the official rulings (2009-10-01) make the difference observable:
 *  - "If the enchanted creature leaves the battlefield before the ability resolves, the ability
 *    continues to resolve. The enchanted creature deals damage to the targeted creature equal to
 *    the power the enchanted creature had as it last existed on the battlefield." A fight
 *    (CR 701.13b) would instead deal no damage at all in that case.
 *  - "You may have the enchanted creature target itself with its own ability. If you do, it will
 *    deal damage to itself equal to its power, then immediately do it again." Two separate
 *    [DealDamageEffect]s reproduce that double hit; a fight would not.
 *  - Only the second creature is targeted, so if *it* becomes an illegal target the whole ability
 *    is countered on resolution and the enchanted creature takes nothing — the ordinary
 *    single-target fizzle, which falls out of the target requirement for free.
 *
 * The ability is granted to the enchanted creature ([GrantActivatedAbility]), so its controller —
 * not the Aura's controller — is the one who activates it, and [DynamicAmounts.sourcePower] reads
 * the enchanted creature (with activation-time LKI if it has left by resolution).
 */
val PredatoryUrge = card("Predatory Urge") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has \"{T}: This creature deals damage equal to its power to target " +
        "creature. That creature deals damage equal to its power to this creature.\""

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Tap,
                effect = DealDamageEffect(
                    amount = DynamicAmounts.sourcePower(),
                    target = EffectTarget.ContextTarget(0),
                    // Default damage source is the ability source id. Do **not** set
                    // damageSource = Self: resolveTarget(Self) returns null once the creature
                    // has left, and DealDamageExecutor then no-ops the instruction — wrong for
                    // the "leaves before resolution" ruling, which still deals LKI power.
                ).then(
                    DealDamageEffect(
                        amount = DynamicAmounts.targetPower(0),
                        target = EffectTarget.Self,
                        damageSource = EffectTarget.ContextTarget(0)
                    )
                ),
                targetRequirements = listOf(TargetCreature())
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "175"
        artist = "Scott Chou"
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d7f8adaa-2852-4e9c-b91d-ce80a21859c9.jpg?1783942133"

        ruling("2009-10-01", "The controller of the enchanted creature may activate the ability, not the controller of Predatory Urge.")
        ruling("2009-10-01", "If the enchanted creature's ability is activated, that creature is the one that will deal and be dealt damage when the ability resolves. It doesn't matter if Predatory Urge leaves the battlefield or somehow becomes attached to another creature by that time.")
        ruling("2009-10-01", "If the targeted creature leaves the battlefield (or otherwise becomes an illegal target) before the ability resolves, the ability doesn't resolve. The enchanted creature isn't dealt damage.")
        ruling("2009-10-01", "On the other hand, if the enchanted creature leaves the battlefield before the ability resolves, the ability continues to resolve. The enchanted creature deals damage to the targeted creature equal to the power the enchanted creature had as it last existed on the battlefield.")
        ruling("2009-10-01", "You may have the enchanted creature target itself with its own ability. If you do, it will deal damage to itself equal to its power, then immediately do it again.")
    }
}
