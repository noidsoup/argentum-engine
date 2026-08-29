package com.wingedsheep.mtg.sets.definitions.apc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Last Stand
 * {W}{U}{B}{R}{G}
 * Sorcery
 *
 * Target opponent loses 2 life for each Swamp you control. Last Stand deals damage to target
 * creature equal to the number of Mountains you control. Create a 1/1 green Saproling creature
 * token for each Forest you control. You gain 2 life for each Plains you control. Draw a card
 * for each Island you control, then discard that many cards.
 */
val LastStand = card("Last Stand") {
    manaCost = "{W}{U}{B}{R}{G}"
    colorIdentity = "WUBRG"
    typeLine = "Sorcery"
    oracleText = "Target opponent loses 2 life for each Swamp you control. Last Stand deals damage " +
        "to target creature equal to the number of Mountains you control. Create a 1/1 green " +
        "Saproling creature token for each Forest you control. You gain 2 life for each Plains " +
        "you control. Draw a card for each Island you control, then discard that many cards."

    spell {
        val opponent = target("opponent", TargetPlayer())
        val creature = target("creature", TargetCreature(filter = TargetFilter.Creature))

        val swamps = DynamicAmount.AggregateBattlefield(
            player = Player.You,
            filter = GameObjectFilter.Land.withSubtype(Subtype.SWAMP),
        )
        val mountains = DynamicAmount.AggregateBattlefield(
            player = Player.You,
            filter = GameObjectFilter.Land.withSubtype(Subtype.MOUNTAIN),
        )
        val forests = DynamicAmount.AggregateBattlefield(
            player = Player.You,
            filter = GameObjectFilter.Land.withSubtype(Subtype.FOREST),
        )
        val plains = DynamicAmount.AggregateBattlefield(
            player = Player.You,
            filter = GameObjectFilter.Land.withSubtype(Subtype.PLAINS),
        )
        val islands = DynamicAmount.AggregateBattlefield(
            player = Player.You,
            filter = GameObjectFilter.Land.withSubtype(Subtype.ISLAND),
        )

        effect = Effects.Composite(
            Effects.LoseLife(DynamicAmount.Multiply(swamps, 2), opponent),
            DealDamageEffect(mountains, creature),
            Effects.CreateToken(
                count = forests,
                power = 1,
                toughness = 1,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Saproling"),
                imageUri = "https://cards.scryfall.io/normal/front/6/2/622759a9-e68b-48c1-8e03-beaab0a52556.jpg?1783942552",
            ),
            Effects.GainLife(DynamicAmount.Multiply(plains, 2), EffectTarget.Controller),
            Effects.Composite(
                DrawCardsEffect(islands),
                Effects.Discard(islands),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "107"
        artist = "Ron Spencer"
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7dc3d054-6266-4ce0-89ed-f8b170794f2e.jpg?1783945333"
    }
}
