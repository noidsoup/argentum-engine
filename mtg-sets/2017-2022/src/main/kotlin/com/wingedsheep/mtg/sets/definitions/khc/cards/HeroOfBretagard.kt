package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.effects.IterationSpace
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Hero of Bretagard — Kaldheim Commander (KHC) #4
 * {2}{W} · Creature — Human Warrior · 1/1
 *
 * Whenever one or more cards are put into exile from your hand or a spell or ability you control
 * exiles one or more permanents from the battlefield, put that many +1/+1 counters on this creature.
 * As long as this creature has five or more counters on it, it has flying and is an Angel in
 * addition to its other types.
 * As long as this creature has ten or more counters on it, it has indestructible and is a God in
 * addition to its other types.
 */
val HeroOfBretagard = card("Hero of Bretagard") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Warrior"
    power = 1
    toughness = 1
    oracleText = "Whenever one or more cards are put into exile from your hand or a spell or ability " +
        "you control exiles one or more permanents from the battlefield, put that many +1/+1 counters " +
        "on this creature.\n" +
        "As long as this creature has five or more counters on it, it has flying and is an Angel in " +
        "addition to its other types.\n" +
        "As long as this creature has ten or more counters on it, it has indestructible and is a God " +
        "in addition to its other types."

    triggeredAbility {
        trigger = Triggers.CardsPutIntoExileFromHandOrByYou()
        effect = Effects.AddDynamicCounters(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            amount = DynamicAmount.DistinctEntitiesInCollections(
                listOf(IterationSpace.TRIGGER_CAPTURED_COLLECTION)
            ),
            target = EffectTarget.Self,
        )
        description = "Whenever one or more cards are put into exile from your hand or a spell or " +
            "ability you control exiles one or more permanents from the battlefield, put that many " +
            "+1/+1 counters on this creature."
    }

    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(CounterTypeFilter.Any, 5)
        ability = GrantKeyword(Keyword.FLYING, Filters.Self)
    }
    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(CounterTypeFilter.Any, 5)
        ability = GrantSubtype("Angel", Filters.Self)
    }
    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(CounterTypeFilter.Any, 10)
        ability = GrantKeyword(Keyword.INDESTRUCTIBLE, Filters.Self)
    }
    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(CounterTypeFilter.Any, 10)
        ability = GrantSubtype("God", Filters.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "4"
        artist = "Heonhwa"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/4458c16c-b71d-481d-863b-f2e3b1320178.jpg?1783928340"
    }
}
