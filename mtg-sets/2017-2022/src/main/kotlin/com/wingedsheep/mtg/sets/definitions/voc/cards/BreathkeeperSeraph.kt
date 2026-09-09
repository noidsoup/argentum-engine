package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.soulbond
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Breathkeeper Seraph
 * {4}{W}{W}
 * Creature — Angel
 * 4/4
 *
 * Flying, soulbond
 * As long as Breathkeeper Seraph is paired with another creature, each of those creatures has
 * "When this creature dies, you may return it to the battlefield under its owner's control at
 * the beginning of your next upkeep."
 *
 * [GrantTriggeredAbility] over [GroupFilter.soulbondPair] — the same shape as [ThunderingMightmare]
 * and [MiragePhalanx]. [TriggerBinding.SELF] makes "this creature" mean whichever paired half died.
 * The printed "you may" is a [Gate.MayDecide] around scheduling the delayed upkeep return; the
 * return itself is [CreateDelayedTriggerEffect] at [Step.UPKEEP] gated to [Player.You].
 */
val BreathkeeperSeraph = card("Breathkeeper Seraph") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 4
    toughness = 4
    oracleText =
        "Flying, soulbond (You may pair this creature with another unpaired creature when either " +
            "enters. They remain paired for as long as you control both of them.)\n" +
            "As long as Breathkeeper Seraph is paired with another creature, each of those creatures has " +
            "\"When this creature dies, you may return it to the battlefield under its owner's " +
            "control at the beginning of your next upkeep.\""

    keywords(Keyword.FLYING)
    soulbond()

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.Dies.event,
                binding = TriggerBinding.SELF,
                effect = GatedEffect(
                    gate = Gate.MayDecide(),
                    then = CreateDelayedTriggerEffect(
                        step = Step.UPKEEP,
                        fireOnPlayer = EffectTarget.PlayerRef(Player.You),
                        effect = Effects.Move(
                            EffectTarget.Self,
                            Zone.BATTLEFIELD,
                            fromZone = Zone.GRAVEYARD,
                        ),
                    ),
                ),
                descriptionOverride =
                    "When this creature dies, you may return it to the battlefield under its " +
                    "owner's control at the beginning of your next upkeep.",
            ),
            filter = GroupFilter.soulbondPair(),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "31"
        artist = "Alexander Mokhov"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/992244dc-d988-41ce-9e3d-cd8034ccc50f.jpg?1783924997"
    }
}
