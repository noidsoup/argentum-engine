package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Emancipation Angel
 * {1}{W}{W}
 * Creature — Angel
 * 3/3
 * Flying
 * When this creature enters, return a permanent you control to its owner's hand.
 */
val EmancipationAngel = card("Emancipation Angel") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText = "Flying\nWhen this creature enters, return a permanent you control to its owner's hand."
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "a permanent you control",
            TargetPermanent(filter = TargetFilter.Permanent.youControl()),
        )
        effect = Effects.Move(t, Zone.HAND)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "19"
        artist = "Scott Chou"
        flavorText = "\"You have done your best. I give you leave to rest.\""
        imageUri =
            "https://cards.scryfall.io/normal/front/7/a/7a4bc00e-28ca-4152-b832-f36425d2b615.jpg?1783940736"
    }
}
