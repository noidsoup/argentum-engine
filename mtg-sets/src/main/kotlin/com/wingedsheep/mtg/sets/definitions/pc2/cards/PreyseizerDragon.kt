package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDevour
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Preyseizer Dragon
 * {4}{R}{R}
 * Creature — Dragon
 * 4/4
 *
 * Flying
 * Devour 2 (As this creature enters, you may sacrifice any number of creatures. It enters with
 * twice that many +1/+1 counters on it.)
 * Whenever this creature attacks, it deals damage to any target equal to the number of +1/+1
 * counters on this creature.
 */
val PreyseizerDragon = card("Preyseizer Dragon") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    oracleText = "Flying\n" +
        "Devour 2 (As this creature enters, you may sacrifice any number of creatures. " +
        "It enters with twice that many +1/+1 counters on it.)\n" +
        "Whenever this creature attacks, it deals damage to any target equal to the number of " +
        "+1/+1 counters on this creature."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING, Keyword.DEVOUR)
    keywordAbility(KeywordAbility.devour(2))

    replacementEffect(EntersWithDevour(multiplier = 2))

    triggeredAbility {
        trigger = Triggers.Attacks
        val target = target("any target", Targets.Any)
        effect = DealDamageEffect(
            amount = DynamicAmounts.countersOnSelf(CounterTypeFilter.PlusOnePlusOne),
            target = target,
        )
        description = "Whenever this creature attacks, it deals damage to any target equal to the " +
            "number of +1/+1 counters on this creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "50"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a1c0caa9-f124-4859-881b-72ecaf8084bb.jpg?1783940617"
    }
}
