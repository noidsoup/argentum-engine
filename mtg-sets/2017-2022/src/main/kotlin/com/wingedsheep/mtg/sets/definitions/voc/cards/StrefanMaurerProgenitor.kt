package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalOnCollectionEffect
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.TurnTracker

/**
 * Strefan, Maurer Progenitor
 * {2}{B}{R}
 * Legendary Creature — Vampire Noble
 * 3/2
 *
 * Flying
 * At the beginning of your end step, create a Blood token for each player who lost life this turn.
 * Whenever Strefan attacks, you may sacrifice two Blood tokens. If you do, you may put a Vampire
 * card from your hand onto the battlefield tapped and attacking. It gains indestructible until end
 * of turn.
 *
 * "Each player who lost life" counts every seat that lost life at least once this turn, including
 * you — `TurnTracking(Player.Each, LIFE_LOST)` sums the per-player 0/1 indicators across the
 * table. The attack ability is Bloodcrazed Socialite's sacrifice gate over two Blood tokens,
 * chaining into Shadowfax's `putFromHand(entersAttacking = true)` and Incandescent Soulstoke's
 * `PipelineTarget("putting", 0)` indestructible grant.
 */
val StrefanMaurerProgenitor = card("Strefan, Maurer Progenitor") {
    manaCost = "{2}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Vampire Noble"
    power = 3
    toughness = 2
    oracleText = "Flying\n" +
        "At the beginning of your end step, create a Blood token for each player who lost life " +
        "this turn.\n" +
        "Whenever Strefan attacks, you may sacrifice two Blood tokens. If you do, you may put a " +
        "Vampire card from your hand onto the battlefield tapped and attacking. It gains " +
        "indestructible until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.CreateBlood(
            DynamicAmount.TurnTracking(Player.Each, TurnTracker.LIFE_LOST)
        )
        description = "At the beginning of your end step, create a Blood token for each player " +
            "who lost life this turn."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = GatedEffect(
            gate = Gate.MayPay(
                SacrificeEffect(
                    filter = GameObjectFilter.Artifact.withSubtype("Blood"),
                    count = 2,
                )
            ),
            then = Patterns.Hand.putFromHand(
                filter = GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE),
                entersAttacking = true,
            ).then(
                ConditionalOnCollectionEffect(
                    collection = "putting",
                    ifNotEmpty = Effects.GrantKeyword(
                        keyword = Keyword.INDESTRUCTIBLE,
                        target = EffectTarget.PipelineTarget("putting", 0),
                        duration = Duration.EndOfTurn,
                    )
                )
            )
        )
        description = "Whenever Strefan attacks, you may sacrifice two Blood tokens. If you do, " +
            "you may put a Vampire card from your hand onto the battlefield tapped and attacking. " +
            "It gains indestructible until end of turn."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "2"
        artist = "Chris Rallis"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f54c6ce-fde4-47ef-a106-5c68b4397f99.jpg?1783925008"
    }
}
