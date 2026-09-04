package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Elvish Rejuvenator
 * {2}{G}
 * Creature — Elf Druid
 * 1/1
 *
 * When this creature enters, look at the top five cards of your library. You may put a land card
 * from among them onto the battlefield tapped. Put the rest on the bottom of your library in a
 * random order.
 */
val ElvishRejuvenator = card("Elvish Rejuvenator") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Druid"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, look at the top five cards of your library. You may put " +
        "a land card from among them onto the battlefield tapped. Put the rest on the bottom of your " +
        "library in a random order."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopAndTakeMatching(
            count = DynamicAmount.Fixed(5),
            filter = Filters.Land,
            prompt = "You may put a land card from among them onto the battlefield tapped",
            selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
            keepDestination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = ZonePlacement.Tapped),
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
            restOrder = CardOrder.Random
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "180"
        artist = "Winona Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0299b00-b16c-4e7d-b67a-ec160ea81a54.jpg?1783934536"
    }
}
