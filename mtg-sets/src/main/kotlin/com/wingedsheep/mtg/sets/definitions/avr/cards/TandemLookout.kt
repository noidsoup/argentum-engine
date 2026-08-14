package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.soulbond
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Tandem Lookout
 * {2}{U}
 * Creature — Human Scout
 * 2/1
 * Soulbond (You may pair this creature with another unpaired creature when either enters.
 *   They remain paired for as long as you control both of them.)
 * As long as Tandem Lookout is paired with another creature, each of those creatures has
 *   "Whenever this creature deals damage to an opponent, draw a card."
 *
 * Canonical printing: AVR #80 (oracle_id 1576f031-…).
 */
val TandemLookout = card("Tandem Lookout") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Scout"
    oracleText =
        "Soulbond (You may pair this creature with another unpaired creature when either enters. " +
            "They remain paired for as long as you control both of them.)\n" +
            "As long as Tandem Lookout is paired with another creature, each of those creatures has " +
            "\"Whenever this creature deals damage to an opponent, draw a card.\""
    power = 2
    toughness = 1

    soulbond()

    val drawOnDamageToOpponent = TriggeredAbility.create(
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Any,
            recipient = RecipientFilter.Opponent,
        ).event,
        binding = Triggers.dealsDamage(
            damageType = DamageType.Any,
            recipient = RecipientFilter.Opponent,
        ).binding,
        effect = Effects.DrawCards(1),
    )

    staticAbility {
        condition = Conditions.SourceIsPaired
        ability = GrantTriggeredAbility(drawOnDamageToOpponent, Filters.Self)
    }
    staticAbility {
        condition = Conditions.SourceIsPaired
        ability = GrantTriggeredAbility(drawOnDamageToOpponent, Filters.SoulbondPartner)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "80"
        artist = "Kev Walker"
        imageUri =
            "https://cards.scryfall.io/normal/front/8/3/83564e67-2677-4955-a3b9-3b221dbb100b.jpg?1783940709"
    }
}
