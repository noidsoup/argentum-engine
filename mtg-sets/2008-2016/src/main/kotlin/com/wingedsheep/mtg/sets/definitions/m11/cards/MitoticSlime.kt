package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Mitotic Slime
 * {4}{G}
 * Creature — Ooze
 * 4/4
 * When this creature dies, create two 2/2 green Ooze creature tokens. They have "When this token
 * dies, create two 1/1 green Ooze creature tokens."
 */
val MitoticSlime = card("Mitotic Slime") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ooze"
    oracleText =
        "When this creature dies, create two 2/2 green Ooze creature tokens. They have " +
            "\"When this token dies, create two 1/1 green Ooze creature tokens.\""
    power = 4
    toughness = 4
    triggeredAbility {
        trigger = Triggers.Dies
        effect = CreateTokenEffect(
            count = DynamicAmount.Fixed(2),
            power = 2,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Ooze"),
            triggeredAbilities = listOf(
                TriggeredAbility.create(
                    trigger = Triggers.Dies.event,
                    binding = Triggers.Dies.binding,
                    effect = Effects.CreateToken(
                        power = 1,
                        toughness = 1,
                        colors = setOf(Color.GREEN),
                        creatureTypes = setOf("Ooze"),
                        count = 2,
                    ),
                ),
            ),
        )
    }
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "185"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/967c8cfa-76b8-4a17-be63-947490b64d85.jpg"
    }
}
