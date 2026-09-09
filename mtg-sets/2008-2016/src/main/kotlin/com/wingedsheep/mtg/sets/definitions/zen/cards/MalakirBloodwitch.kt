package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Malakir Bloodwitch
 * {3}{B}{B}
 * Creature — Vampire Shaman
 * 4/4
 *
 * Flying, protection from white
 * When this creature enters, each opponent loses life equal to the number of Vampires you
 * control. You gain life equal to the life lost this way.
 *
 * Canonical printing: Zendikar (ZEN). The drain is [Effects.DrainLife] with X =
 * [DynamicAmount.AggregateBattlefield] over Vampire permanents you control (Champion of Dusk
 * count shape; Exsanguinate gain shape).
 */
val MalakirBloodwitch = card("Malakir Bloodwitch") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Shaman"
    oracleText = "Flying, protection from white\n" +
        "When this creature enters, each opponent loses life equal to the number of Vampires you " +
        "control. You gain life equal to the life lost this way."
    power = 4
    toughness = 4

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.protectionFrom(Color.WHITE))

    val vampireCount = DynamicAmount.AggregateBattlefield(
        Player.You,
        GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE),
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrainLife(vampireCount)
        description = "When this creature enters, each opponent loses life equal to the number " +
            "of Vampires you control. You gain life equal to the life lost this way."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "100"
        artist = "Shelly Wan"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/865ac36f-4c61-468b-a2ed-ed2d1bbbf4e3.jpg?1783942152"
        ruling(
            "2009-10-01",
            "The number of Vampires you control is counted only as Malakir Bloodwitch's ability " +
                "resolves. If Malakir Bloodwitch is still on the battlefield, it'll count itself.",
        )
    }
}
