package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Chandra, Bold Pyromancer
 * {4}{R}{R}
 * Legendary Planeswalker — Chandra
 * Starting loyalty: 5
 *
 * +1: Add {R}{R}. Chandra deals 2 damage to target player.
 * −3: Chandra deals 3 damage to target creature or planeswalker.
 * −7: Chandra deals 10 damage to target player and each creature and planeswalker they control.
 */
val ChandraBoldPyromancer = card("Chandra, Bold Pyromancer") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Planeswalker — Chandra"
    startingLoyalty = 5
    oracleText = "+1: Add {R}{R}. Chandra deals 2 damage to target player.\n" +
        "\u22123: Chandra deals 3 damage to target creature or planeswalker.\n" +
        "\u22127: Chandra deals 10 damage to target player and each creature and planeswalker they control."

    loyaltyAbility(+1) {
        val player = target("target player", Targets.Player)
        effect = Effects.Composite(
            Effects.AddMana(Color.RED, 2),
            Effects.DealDamage(2, player),
        )
    }

    loyaltyAbility(-3) {
        val target = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
        effect = Effects.DealDamage(3, target)
    }

    loyaltyAbility(-7) {
        val player = target("target player", Targets.Player)
        effect = Effects.DealDamage(10, player)
            .then(
                Patterns.Group.dealDamageToAll(
                    10,
                    GroupFilter(
                        GameObjectFilter.CreatureOrPlaneswalker.targetPlayerControls(player)
                    ),
                )
            )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "275"
        artist = "Zack Stella"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba9d2384-5c3f-4eb1-86b4-26ee13f1c767.jpg?1783934935"
    }
}
