package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Aggressive Instinct — Global Series: Jiang Yanggu & Mu Yanling #34
 * {1}{G} · Sorcery
 *
 * Target creature you control deals damage equal to its power to target creature you don't control.
 *
 * Same one-sided "deal power as damage" shape as Stew the Coneys / Clear Shot's second clause —
 * t1 must be youControl, t2 must be opponentControls (never youControl twice).
 */
val AggressiveInstinct = card("Aggressive Instinct") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Target creature you control deals damage equal to its power to target creature you don't control."

    spell {
        val myCreature = target("creature you control", Targets.CreatureYouControl)
        val theirCreature = target("creature you don't control", Targets.CreatureOpponentControls)
        effect = Effects.DealDamage(
            amount = DynamicAmount.EntityProperty(EntityReference.Target(0), EntityNumericProperty.Power),
            target = theirCreature,
            damageSource = myCreature,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "34"
        artist = "Wolk Sheep"
        flavorText = "\"Mowu was wary that day, as though he sensed the danger ahead.\"\n—Jiang Yanggu's travelogue"
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40ab4bcf-e6b9-4368-97d5-56862713a66a.jpg?1783934625"
    }
}
