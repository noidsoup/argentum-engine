package com.wingedsheep.mtg.sets.definitions.all.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerTiming
import com.wingedsheep.sdk.scripting.effects.DrawUpToEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Arcane Denial — Alliances #22a
 * {1}{U} · Instant
 *
 * Counter target spell. Its controller may draw up to two cards at the beginning of the next
 * turn's upkeep.
 * You draw a card at the beginning of the next turn's upkeep.
 *
 * "The next turn's upkeep" is one shared step — [DelayedTriggerTiming.NEXT_TURN] with
 * [Step.UPKEEP] and no [CreateDelayedTriggerEffect.fireOnPlayer] gate, not "your next upkeep".
 * The countered spell's controller is frozen at counter time via
 * [EffectTarget.PlayerRef] over [Player.ControllerOf] inside the delayed [DrawUpToEffect].
 */
val ArcaneDenial = card("Arcane Denial") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell. Its controller may draw up to two cards at the beginning " +
        "of the next turn's upkeep.\nYou draw a card at the beginning of the next turn's upkeep."

    spell {
        target("target spell", Targets.Spell)
        effect = Effects.CounterSpell()
            .then(
                CreateDelayedTriggerEffect(
                    step = Step.UPKEEP,
                    timing = DelayedTriggerTiming.NEXT_TURN,
                    effect = MayEffect(
                        DrawUpToEffect(
                            maxCards = 2,
                            target = EffectTarget.PlayerRef(Player.ControllerOf("target spell")),
                        ),
                        decisionMaker = EffectTarget.PlayerRef(Player.ControllerOf("target spell")),
                    ),
                ),
            )
            .then(
                CreateDelayedTriggerEffect(
                    step = Step.UPKEEP,
                    timing = DelayedTriggerTiming.NEXT_TURN,
                    effect = Effects.DrawCards(1),
                ),
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22a"
        artist = "Richard Kane Ferguson"
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0c5728e-43e7-417a-ba18-5038345cec67.jpg?1783947197"
    }
}
