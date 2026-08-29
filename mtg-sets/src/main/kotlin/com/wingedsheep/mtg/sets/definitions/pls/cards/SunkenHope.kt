package com.wingedsheep.mtg.sets.definitions.pls.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sunken Hope
 * {3}{U}{U}
 * Enchantment
 *
 * At the beginning of each player's upkeep, that player returns a creature they control to its
 * owner's hand.
 */
val SunkenHope = card("Sunken Hope") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "At the beginning of each player's upkeep, that player returns a creature they control to its owner's hand."

    triggeredAbility {
        trigger = Triggers.EachUpkeep
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.BattlefieldMatching(
                    filter = GameObjectFilter.Creature,
                    player = Player.TriggeringPlayer,
                ),
                storeAs = "bounceCandidates",
            ),
            SelectFromCollectionEffect(
                from = "bounceCandidates",
                selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                storeSelected = "bounced",
                chooser = Chooser.TriggeringPlayer,
                prompt = "Return a creature you control to its owner's hand",
                useTargetingUI = true,
            ),
            MoveCollectionEffect(
                from = "bounced",
                destination = CardDestination.ToZone(Zone.HAND),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "37"
        artist = "Greg Staples"
        flavorText = "While Dominarians fought for their world, Rath stole it from under their feet."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f12ac0c-cfe6-4f08-b6df-20be4ce83e8c.jpg?1783945623"
    }
}
