package com.wingedsheep.mtg.sets.definitions.ulg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Karmic Guide
 * {3}{W}{W}
 * Creature — Angel Spirit
 * 2/2
 * Flying, protection from black
 * Echo {3}{W}{W}
 * When this creature enters, return target creature card from your graveyard to the battlefield.
 *
 * Canonical printing lives here (Urza's Legacy, 1999 — earliest real-expansion printing). VOC and
 * other sets are reprints ([Printing] rows only).
 *
 * Echo is engine-live via [KeywordAbility.echo] — do not hand-write the upkeep trigger. Protection
 * from black is [KeywordAbility.protectionFrom] over [Color.BLACK], not a bare keyword enum.
 */
val KarmicGuide = card("Karmic Guide") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel Spirit"
    power = 2
    toughness = 2
    oracleText = "Flying, protection from black\n" +
        "Echo {3}{W}{W} (At the beginning of your upkeep, if this came under your control since the " +
        "beginning of your last upkeep, sacrifice it unless you pay its echo cost.)\n" +
        "When this creature enters, return target creature card from your graveyard to the battlefield."

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.protectionFrom(Color.BLACK))
    keywordAbility(KeywordAbility.echo("{3}{W}{W}"))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target creature card in your graveyard", Targets.CreatureCardInYourGraveyard)
        effect = Effects.PutOntoBattlefieldFromGraveyard(creature)
        description = "When this creature enters, return target creature card from your graveyard " +
            "to the battlefield."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "11"
        artist = "Heather Hudson"
        imageUri = "https://cards.scryfall.io/normal/front/7/7/77d23045-905b-44cb-9af9-cc6ad717477d.jpg?1783946252"
    }
}
