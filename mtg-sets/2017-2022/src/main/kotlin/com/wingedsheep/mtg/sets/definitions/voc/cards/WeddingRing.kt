package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.LifeGainEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Wedding Ring
 * {2}{W}{W}
 * Artifact
 *
 * When this artifact enters, if it was cast, target opponent creates a token that's a copy of it.
 * Whenever an opponent who controls an artifact named Wedding Ring draws a card during their turn,
 * you draw a card.
 * Whenever an opponent who controls an artifact named Wedding Ring gains life during their turn,
 * you gain that much life.
 */
val WeddingRing = card("Wedding Ring") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, if it was cast, target opponent creates a token that's " +
        "a copy of it.\n" +
        "Whenever an opponent who controls an artifact named Wedding Ring draws a card during their " +
        "turn, you draw a card.\n" +
        "Whenever an opponent who controls an artifact named Wedding Ring gains life during their " +
        "turn, you gain that much life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.WasCast
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.CreateTokenCopyOfTarget(
            target = EffectTarget.Self,
            controller = opponent,
        )
        description = "When this artifact enters, if it was cast, target opponent creates a token " +
            "that's a copy of it."
    }

    triggeredAbility {
        trigger = Triggers.OpponentDraws
        triggerRestriction = Conditions.All(
            Conditions.IsNotYourTurn,
            Exists(
                Player.TriggeringPlayer,
                Zone.BATTLEFIELD,
                GameObjectFilter.Artifact.named("Wedding Ring"),
            ),
        )
        effect = Effects.DrawCards(1)
        description = "Whenever an opponent who controls an artifact named Wedding Ring draws a " +
            "card during their turn, you draw a card."
    }

    triggeredAbility {
        trigger = TriggerSpec(
            event = LifeGainEvent(Player.EachOpponent),
            binding = TriggerBinding.ANY,
        )
        triggerRestriction = Conditions.All(
            Conditions.IsNotYourTurn,
            Exists(
                Player.TriggeringPlayer,
                Zone.BATTLEFIELD,
                GameObjectFilter.Artifact.named("Wedding Ring"),
            ),
        )
        effect = Effects.GainLife(DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_LIFE_GAINED))
        description = "Whenever an opponent who controls an artifact named Wedding Ring gains life " +
            "during their turn, you gain that much life."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "32"
        artist = "Olena Richards"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f0b1400-0608-47fd-9c73-b7730bcf6b7f.jpg?1783924997"
    }
}
