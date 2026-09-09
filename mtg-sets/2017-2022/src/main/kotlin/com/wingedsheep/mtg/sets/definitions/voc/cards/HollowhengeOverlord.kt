package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hollowhenge Overlord
 * {4}{G}{G}
 * Creature — Wolf
 * 4/4
 *
 * Flash
 * At the beginning of your upkeep, for each creature you control that's a Wolf or a Werewolf, create
 * a 2/2 green Wolf creature token.
 *
 * The token count is [DynamicAmounts.battlefield] over Wolf-or-Werewolf creatures you control,
 * evaluated at upkeep resolution — Hollowhenge Overlord itself counts if it is still a Wolf.
 */
val HollowhengeOverlord = card("Hollowhenge Overlord") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    oracleText =
        "Flash\n" +
            "At the beginning of your upkeep, for each creature you control that's a Wolf or a " +
            "Werewolf, create a 2/2 green Wolf creature token."
    power = 4
    toughness = 4

    keywords(Keyword.FLASH)

    val wolvesAndWerewolvesYouControl = DynamicAmounts
        .battlefield(Player.You, GameObjectFilter.Creature.withAnySubtype("Wolf", "Werewolf"))
        .count()

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.CreateToken(
            count = wolvesAndWerewolvesYouControl,
            power = 2,
            toughness = 2,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Wolf"),
            controller = EffectTarget.Controller,
            imageUri = "https://cards.scryfall.io/normal/front/d/5/d5f1e139-3054-4273-8a4d-faaaa9c383a8.jpg?1783924694",
        )
        description =
            "At the beginning of your upkeep, for each creature you control that's a Wolf or a " +
                "Werewolf, create a 2/2 green Wolf creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "36"
        artist = "Milivoj \u0106eran"
        flavorText = "The ruins of the town were a notable upgrade from the wolves' forest den."
        imageUri = "https://cards.scryfall.io/normal/front/d/a/dac9c33c-bff1-491a-b369-ad92395283a5.jpg?1783924994"
    }
}
