package com.wingedsheep.mtg.sets.definitions.cns.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Brago, King Eternal — Conspiracy (CNS) #41
 * {2}{W}{U} · Legendary Creature — Spirit Noble · 2/4
 *
 * Flying
 * Whenever Brago deals combat damage to a player, exile any number of target nonland permanents you
 * control, then return those cards to the battlefield under their owner's control.
 *
 * The blink is one atomic gather → exile (linked to this ability) → return-from-linked-exile
 * pipeline (Ghostly Flicker / Lake-town Mariners), seeded from [CardSource.ChosenTargets] because
 * the permanents are targets locked in at trigger resolution. All chosen permanents leave and
 * re-enter as one batch so ETB abilities see the group together.
 */
val BragoKingEternal = card("Brago, King Eternal") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "WU"
    typeLine = "Legendary Creature — Spirit Noble"
    power = 2
    toughness = 4
    oracleText = "Flying\n" +
        "Whenever Brago deals combat damage to a player, exile any number of target nonland " +
        "permanents you control, then return those cards to the battlefield under their owner's " +
        "control."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        target(
            "any number of target nonland permanents you control",
            TargetPermanent(
                unlimited = true,
                optional = true,
                filter = TargetFilter(GameObjectFilter.NonlandPermanent.youControl()),
            ),
        )
        effect = Effects.Pipeline(
            descriptionOverride = "Exile any number of target nonland permanents you control, " +
                "then return those cards to the battlefield under their owner's control",
        ) {
            val chosen = gather(source = CardSource.ChosenTargets)
            exile(chosen, linkToSource = true)
            val returning = gather(source = CardSource.FromLinkedExile())
            move(returning, CardDestination.ToZone(Zone.BATTLEFIELD), underOwnersControl = true)
        }
        description = "Whenever Brago deals combat damage to a player, exile any number of target " +
            "nonland permanents you control, then return those cards to the battlefield under their " +
            "owner's control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "41"
        artist = "Karla Ortiz"
        flavorText = "\"My rule persists beyond death itself.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/6/1691145e-e9c2-45e7-be35-83245a6e5eaf.jpg?1783939373"
    }
}
