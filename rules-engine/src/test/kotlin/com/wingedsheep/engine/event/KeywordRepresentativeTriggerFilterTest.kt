package com.wingedsheep.engine.event

import com.wingedsheep.engine.mechanics.layers.ProjectedState
import com.wingedsheep.engine.mechanics.layers.ProjectedValues
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.MayEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * CR 702.95a — Soulbond is a keyword that represents two triggered abilities. A copy that "loses
 * soulbond" must not keep firing the pairing triggers printed on the card definition.
 */
class KeywordRepresentativeTriggerFilterTest : FunSpec({

    val entityId = EntityId.of("test-entity")

    val soulbondEnterTrigger = TriggeredAbility.create(
        trigger = Triggers.EntersBattlefield.event,
        binding = Triggers.EntersBattlefield.binding,
        effect = Effects.Pipeline {
            val partner = gather(com.wingedsheep.sdk.scripting.GameObjectFilter.Creature.unpaired())
            pairWithSource(partner)
        },
    )

    val soulbondOtherEntersTrigger = TriggeredAbility.create(
        trigger = Triggers.OtherCreatureEnters.event,
        binding = Triggers.OtherCreatureEnters.binding,
        effect = MayEffect(
            Effects.Pipeline {
                val partner = gather(com.wingedsheep.sdk.scripting.effects.CardSource.TriggeringEntity)
                pairWithSource(partner)
            }
        ),
    )

    val unrelatedTrigger = TriggeredAbility.create(
        trigger = Triggers.BeginCombat.event,
        binding = Triggers.BeginCombat.binding,
        effect = Effects.DrawCards(1),
    )

    fun projected(keywords: Set<String> = emptySet()) =
        ProjectedState(
            baseState = GameState(),
            projectedValues = mapOf(entityId to ProjectedValues(keywords = keywords)),
        )

    test("soulbond pairing triggers are kept while the permanent has soulbond") {
        val filtered = KeywordRepresentativeTriggerFilter.filter(
            listOf(soulbondEnterTrigger, soulbondOtherEntersTrigger, unrelatedTrigger),
            entityId,
            projected(setOf(Keyword.SOULBOND.name)),
        )
        filtered shouldHaveSize 3
    }

    test("soulbond pairing triggers are stripped when soulbond is absent") {
        val filtered = KeywordRepresentativeTriggerFilter.filter(
            listOf(soulbondEnterTrigger, soulbondOtherEntersTrigger, unrelatedTrigger),
            entityId,
            projected(),
        )
        filtered shouldBe listOf(unrelatedTrigger)
    }
})
