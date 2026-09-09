package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.GrantTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Predators' Hour
 * {1}{B}
 * Sorcery
 *
 * Until end of turn, creatures you control gain menace and "Whenever this creature deals combat
 * damage to a player, exile the top card of that player's library face down. You may look at and
 * play that card for as long as it remains exiled, and you may spend mana as though it were mana
 * of any color to cast that spell."
 *
 * Like Root Manipulation, this is a one-shot pump applied to each creature the controller has at
 * resolution (Rule 611.2c). Each affected creature receives menace and a granted combat-damage
 * trigger for the duration of the turn. The theft pipeline follows Gonti, Night Minister: gather
 * from the damaged player's library, exile face down with a look grant, then a permanent
 * may-play-from-exile permission with any-color mana for spells.
 */
val PredatorsHour = card("Predators' Hour") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Until end of turn, creatures you control gain menace and \"Whenever this " +
        "creature deals combat damage to a player, exile the top card of that player's library " +
        "face down. You may look at and play that card for as long as it remains exiled, and you " +
        "may spend mana as though it were mana of any color to cast that spell.\""

    spell {
        val stealOnCombatDamage = TriggeredAbility.create(
            trigger = Triggers.DealsCombatDamageToPlayer.event,
            binding = Triggers.DealsCombatDamageToPlayer.binding,
            effect = Effects.Composite(
                listOf(
                    GatherCardsEffect(
                        source = CardSource.TopOfLibrary(
                            count = DynamicAmount.Fixed(1),
                            player = Player.TriggeringPlayer,
                        ),
                        storeAs = "stolenCard",
                    ),
                    MoveCollectionEffect(
                        from = "stolenCard",
                        destination = CardDestination.ToZone(Zone.EXILE, Player.TriggeringPlayer),
                        faceDown = FaceDownMode.HIDDEN,
                        lookableInExile = true,
                    ),
                    GrantMayPlayFromExileEffect(
                        from = "stolenCard",
                        expiry = MayPlayExpiry.Permanent,
                        withAnyManaType = true,
                    ),
                ),
            ),
            descriptionOverride = "Whenever this creature deals combat damage to a player, exile " +
                "the top card of that player's library face down. You may look at and play that " +
                "card for as long as it remains exiled, and you may spend mana as though it were " +
                "mana of any color to cast that spell.",
        )
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Creature.youControl()),
            effect = Effects.Composite(
                Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self),
                GrantTriggeredAbilityEffect(
                    ability = stealOnCombatDamage,
                    target = EffectTarget.Self,
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "21"
        artist = "Tomas Duchek"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/dea6d749-d2a9-43f0-8ab4-ff83ba294564.jpg?1783925002"
    }
}
