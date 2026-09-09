package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rakdos Charm
 * {B}{R}
 * Instant
 *
 * Choose one —
 * • Exile target player's graveyard.
 * • Destroy target artifact.
 * • Each creature deals 1 damage to its controller.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 */
val RakdosCharm = card("Rakdos Charm") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Exile target player's graveyard.\n" +
        "• Destroy target artifact.\n" +
        "• Each creature deals 1 damage to its controller."

    spell {
        modal(chooseCount = 1) {
            mode("Exile target player's graveyard") {
                val player = target("target player", Targets.Player)
                effect = Effects.Composite(
                    GatherCardsEffect(
                        source = CardSource.FromZone(Zone.GRAVEYARD, Player.ContextPlayer(0)),
                        storeAs = "rakdosCharmGraveyard",
                    ),
                    MoveCollectionEffect(
                        from = "rakdosCharmGraveyard",
                        destination = CardDestination.ToZone(Zone.EXILE, Player.ContextPlayer(0)),
                    ),
                )
            }
            mode("Destroy target artifact") {
                val artifact = target("target artifact", Targets.Artifact)
                effect = Effects.Destroy(artifact)
            }
            mode("Each creature deals 1 damage to its controller") {
                effect = Effects.ForEachInGroup(
                    GroupFilter(GameObjectFilter.Creature),
                    Effects.DealDamage(
                        1,
                        EffectTarget.PlayerRef(Player.ControllerOfIterationEntity),
                        damageSource = EffectTarget.Self,
                    ),
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "184"
        artist = "Zoltan Boros"
        flavorText = "\"Let all feel joy in pain.\"\n—Rakdos"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fcd4394-d22d-4eec-ad73-ffaf10ad60de.jpg?1783940335"
    }
}
