package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Guard Gomazoa
 * {2}{U}
 * Creature — Jellyfish
 * 1/3
 *
 * Defender, flying
 * Prevent all combat damage that would be dealt to this creature.
 *
 * Continuous [PreventDamage] replacement scoped to combat damage dealt to Self — the same
 * shape as Diamond Weapon's Immune clause (Fog Bank's "dealt to" half without the "dealt by"
 * twin).
 */
val GuardGomazoa = card("Guard Gomazoa") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Jellyfish"
    power = 1
    toughness = 3
    oracleText = "Defender, flying\n" +
        "Prevent all combat damage that would be dealt to this creature."

    keywords(Keyword.DEFENDER, Keyword.FLYING)

    replacementEffect(
        PreventDamage(
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.Self,
                damageType = DamageType.Combat
            )
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "70"
        artist = "Rob Alexander"
        flavorText = "It lingers near the outposts of the Makindi Trenches, inadvertently " +
            "granting another layer of defense."
        imageUri = "https://cards.scryfall.io/normal/front/0/5/0523e59a-91f7-4893-a4fe-8814a745b422.jpg?1783941996"
    }
}
