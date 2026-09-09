package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Midnight Clock
 * {2}{U}
 * Artifact
 *
 * {T}: Add {U}.
 * {2}{U}: Put an hour counter on this artifact.
 * At the beginning of each upkeep, put an hour counter on this artifact.
 * When the twelfth hour counter is put on this artifact, shuffle your hand and graveyard into
 * your library, then draw seven cards. Exile this artifact.
 */
val MidnightClock = card("Midnight Clock") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact"
    oracleText = "{T}: Add {U}.\n" +
        "{2}{U}: Put an hour counter on this artifact.\n" +
        "At the beginning of each upkeep, put an hour counter on this artifact.\n" +
        "When the twelfth hour counter is put on this artifact, shuffle your hand and graveyard " +
        "into your library, then draw seven cards. Exile this artifact."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        effect = Effects.AddCounters(Counters.HOUR, 1, EffectTarget.Self)
        description = "Put an hour counter on this artifact."
    }

    triggeredAbility {
        trigger = Triggers.EachUpkeep
        effect = Effects.AddCounters(Counters.HOUR, 1, EffectTarget.Self)
        description = "At the beginning of each upkeep, put an hour counter on this artifact."
    }

    triggeredAbility {
        trigger = Triggers.countersPlacedOn(
            filter = GameObjectFilter.Any,
            counterType = Counters.HOUR,
            firstTimeEachTurn = false,
            binding = TriggerBinding.SELF,
        )
        triggerRestriction = Conditions.SourceCounterCountAtLeast(Counters.HOUR, 12)
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromMultipleZones(
                    zones = listOf(Zone.HAND, Zone.GRAVEYARD),
                    player = Player.You,
                ),
                storeAs = "midnightClockShuffleCards",
            ),
            MoveCollectionEffect(
                from = "midnightClockShuffleCards",
                destination = CardDestination.ToZone(Zone.LIBRARY, Player.You, ZonePlacement.Shuffled),
            ),
            Effects.DrawCards(7),
            Effects.Exile(EffectTarget.Self),
        )
        description = "When the twelfth hour counter is put on this artifact, shuffle your hand " +
            "and graveyard into your library, then draw seven cards. Exile this artifact."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "54"
        artist = "Alexander Forssberg"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f7f1148-7b1b-4969-a2f8-428de1e2e8ff.jpg?1783932655"
    }
}
