package com.wingedsheep.mtg.sets.definitions.bbd.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Arcane Artisan — Battlebond (BBD) #33
 * {2}{U} · Creature — Human Wizard · 0/3
 *
 * {2}{U}, {T}: Target player draws a card, then exiles a card from their hand. If a creature card
 * is exiled this way, that player creates a token that's a copy of that card.
 * When this creature leaves the battlefield, exile all tokens created with it at the beginning of
 * the next end step.
 */
val ArcaneArtisan = card("Arcane Artisan") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 0
    toughness = 3
    oracleText = "{2}{U}, {T}: Target player draws a card, then exiles a card from their hand. " +
        "If a creature card is exiled this way, that player creates a token that's a copy of " +
        "that card.\n" +
        "When this creature leaves the battlefield, exile all tokens created with it at the " +
        "beginning of the next end step."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{U}"), Costs.Tap)
        val player = target("target player", Targets.Player)
        effect = Effects.Pipeline {
            run(Effects.DrawCards(1, player))
            val hand = gather(
                CardSource.FromZone(Zone.HAND, Player.TargetPlayer),
                name = "hand",
            )
            val exiled = chooseExactly(
                count = 1,
                from = hand,
                chooser = Chooser.TargetPlayer,
                prompt = "Choose a card to exile",
                name = "exiled",
            )
            exile(exiled)
            val creatureExiled = filter(exiled, GameObjectFilter.Creature, name = "creatureExiled")
            run(
                ConditionalEffect(
                    condition = whenMatches(creatureExiled, GameObjectFilter.Creature),
                    effect = Effects.CreateTokenCopyOfTarget(
                        target = EffectTarget.PipelineTarget(creatureExiled.key),
                        controller = player,
                        stampCreator = true,
                    ),
                ),
            )
        }
        description = "{2}{U}, {T}: Target player draws a card, then exiles a card from their hand. " +
            "If a creature card is exiled this way, that player creates a token that's a copy of " +
            "that card."
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = CreateDelayedTriggerEffect(
            step = Step.END,
            effect = Effects.Pipeline {
                val tokens = gather(
                    CardSource.BattlefieldMatching(
                        filter = GameObjectFilter.Any.createdBySource(),
                        player = Player.Each,
                    ),
                    name = "artisanTokens",
                )
                exile(tokens)
            },
        )
        description = "When this creature leaves the battlefield, exile all tokens created with it " +
            "at the beginning of the next end step."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "33"
        artist = "Tommy Arnold"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b2441e0-06f6-4455-8fa8-a34c3e241a56.jpg?1783934867"
    }
}
