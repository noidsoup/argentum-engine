package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.scripting.conditions.EntityMatches
import com.wingedsheep.sdk.scripting.conditions.NotCondition
import com.wingedsheep.sdk.scripting.costs.CostAtom
import com.wingedsheep.sdk.scripting.costs.PayCost
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.scripting.predicates.StatePredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Echo [cost] (CR 702.30) as pure data — the triggered ability every echo permanent has and none
 * of them prints as a separate line.
 *
 * A card carrying `Echo {3}{W}{W}` shows one keyword line plus its reminder text; the rules give
 * it one ability:
 *
 * **"At the beginning of your upkeep, if this permanent came under your control since the
 * beginning of your last upkeep, sacrifice it unless you pay [cost]."** ([upkeepAbility])
 *
 * The intervening-`if` is tracked by an engine upkeep-presence marker — a permanent that was
 * already under its controller's control when their previous upkeep step ended does not echo.
 * Entering the battlefield or changing controller clears the marker, so a blinked or stolen
 * echo creature echoes on its new controller's next upkeep.
 *
 * The trigger is synthesized for any permanent whose *projected* keywords include
 * [Keyword.ECHO], the same shape as [Vanishing] and [Flanking]. The echo *cost* is read from
 * the printed [KeywordAbility.Echo] on the card definition, because a projected keyword set
 * carries no cost parameter.
 */
object Echo {

    private val presentAtControllersLastUpkeep = EntityMatches(
        EffectTarget.Self,
        GameObjectFilter.Any.copy(
            statePredicates = listOf(StatePredicate.PresentAtControllersLastUpkeep),
        ),
    )

    /**
     * CR 702.30a — "At the beginning of your upkeep, if this permanent came under your control
     * since the beginning of your last upkeep, sacrifice it unless you pay [cost]."
     */
    fun upkeepAbility(cost: ManaCost, instance: Int = 0): TriggeredAbility = TriggeredAbility(
        id = AbilityId(if (instance == 0) "echo_upkeep" else "echo_upkeep_$instance"),
        trigger = EventPattern.StepEvent(Step.UPKEEP, Player.You),
        binding = TriggerBinding.SELF,
        activeZones = setOf(Zone.BATTLEFIELD),
        interveningIf = NotCondition(presentAtControllersLastUpkeep),
        effect = PayOrSufferEffect(
            cost = PayCost.Atom(CostAtom.Mana(cost)),
            suffer = SacrificeSelfEffect,
        ),
        descriptionOverride = "At the beginning of your upkeep, sacrifice this permanent " +
            "unless you pay $cost.",
    )

    /** Every printed echo cost on [cardDef], in declaration order. */
    fun printedCosts(cardDef: CardDefinition): List<ManaCost> =
        cardDef.keywordAbilities.filterIsInstance<KeywordAbility.Echo>().map { it.cost }
}
