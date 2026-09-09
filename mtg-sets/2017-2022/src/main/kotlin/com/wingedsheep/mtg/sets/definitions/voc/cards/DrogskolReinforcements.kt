package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.melee
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.core.Keyword

/**
 * Drogskol Reinforcements
 * {3}{W}
 * Creature — Spirit Soldier
 * 2/2
 *
 * Melee (Whenever this creature attacks, it gets +1/+1 until end of turn for each opponent you
 * attacked this combat.)
 * Other Spirits you control have melee.
 * Prevent all noncombat damage that would be dealt to Spirits you control.
 */
private val otherSpiritsYouControl = GroupFilter(
    GameObjectFilter.Creature.withSubtype(Subtype.SPIRIT).youControl(),
    excludeSelf = true,
)

val DrogskolReinforcements = card("Drogskol Reinforcements") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Soldier"
    oracleText = "Melee (Whenever this creature attacks, it gets +1/+1 until end of turn for each " +
        "opponent you attacked this combat.)\n" +
        "Other Spirits you control have melee.\n" +
        "Prevent all noncombat damage that would be dealt to Spirits you control."
    power = 2
    toughness = 2

    melee()

    staticAbility {
        ability = GrantKeyword(Keyword.MELEE, otherSpiritsYouControl)
    }

    replacementEffect(
        PreventDamage(
            amount = null,
            appliesTo = EventPattern.DamageEvent(
                recipient = RecipientFilter.Matching(
                    GameObjectFilter.Creature.youControl().withSubtype(Subtype.SPIRIT),
                ),
                damageType = DamageType.NonCombat,
            ),
        ),
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "5"
        artist = "Antonio José Manzanedo"
        imageUri = "https://cards.scryfall.io/normal/front/c/8/c86bdb28-9b0c-426d-b603-cc10cd301cc6.jpg?1783925010"
    }
}
