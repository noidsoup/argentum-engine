package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

/**
 * Mark of Mutiny
 * {2}{R}
 * Sorcery
 *
 * Gain control of target creature until end of turn. Put a +1/+1 counter on it and untap it.
 * That creature gains haste until end of turn.
 */
val MarkOfMutiny = card("Mark of Mutiny") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Gain control of target creature until end of turn. Put a +1/+1 counter on it " +
        "and untap it. That creature gains haste until end of turn. (It can attack and {T} this turn.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.GainControl(creature, Duration.EndOfTurn),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature),
            Effects.Untap(creature),
            Effects.GrantKeyword(Keyword.HASTE, creature),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "137"
        artist = "Mike Bierek"
        flavorText = "The flame of anger is hard to douse once lit."
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58a0a019-239d-428e-85a2-e19cae8f4b58.jpg?1783942142"
    }
}
