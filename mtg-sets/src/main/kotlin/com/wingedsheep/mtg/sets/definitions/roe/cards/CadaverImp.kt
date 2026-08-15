package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Cadaver Imp
 * {1}{B}{B}
 * Creature — Imp
 * 1/1
 * Flying
 * When this creature enters, you may return target creature card from your graveyard to your hand.
 */
val CadaverImp = card("Cadaver Imp") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Imp"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "When this creature enters, you may return target creature card from your graveyard to your hand."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val t = target("target creature card in your graveyard", TargetObject(filter = TargetFilter.CreatureInYourGraveyard))
        effect = Effects.Move(t, Zone.HAND)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Dave Kendall"
        flavorText = "The mouth must be pried open before rigor mortis sets in. Otherwise the returning soul can find no ingress."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b2f83d5-9269-4297-b507-558c2bdec32b.jpg?1783941988"
    }
}
