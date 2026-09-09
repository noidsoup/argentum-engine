package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.conditions.IsInStep
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.EmitLibrarySearchedEventEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Rhoda, Geist Avenger
 * {3}{W}
 * Legendary Creature — Human Soldier
 * 3/3
 *
 * Partner with Timin, Youthful Geist (When this creature enters, target player may put Timin into
 * their hand from their library, then shuffle.)
 * Vigilance
 * Whenever a creature an opponent controls becomes tapped, if it isn't being declared as an
 * attacker, put a +1/+1 counter on Rhoda.
 */
val RhodaGeistAvenger = card("Rhoda, Geist Avenger") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Soldier"
    oracleText = "Partner with Timin, Youthful Geist (When this creature enters, target player may " +
        "put Timin into their hand from their library, then shuffle.)\n" +
        "Vigilance\n" +
        "Whenever a creature an opponent controls becomes tapped, if it isn't being declared as an " +
        "attacker, put a +1/+1 counter on Rhoda."
    power = 3
    toughness = 3

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val chosen = target("target player", TargetPlayer())
        effect = MayEffect(
            Effects.Pipeline {
                val searchPool = gather(
                    CardSource.FromZone(
                        zone = Zone.LIBRARY,
                        player = Player.TargetPlayer,
                        filter = GameObjectFilter.Any.named("Timin, Youthful Geist"),
                    ),
                    name = "timinInLibrary",
                )
                val found = chooseUpTo(
                    count = 1,
                    from = searchPool,
                    chooser = Chooser.TargetPlayer,
                    prompt = "Search your library for a card named Timin, Youthful Geist",
                    name = "timinFound",
                )
                move(
                    from = found,
                    destination = CardDestination.ToZone(Zone.HAND, Player.TargetPlayer),
                    revealed = true,
                )
                run(ShuffleLibraryEffect(chosen))
                run(EmitLibrarySearchedEventEffect)
            },
        )
    }

    triggeredAbility {
        trigger = Triggers.becomesTapped(
            binding = TriggerBinding.ANY,
            filter = GameObjectFilter.Creature.opponentControls(),
        )
        // Attack taps stamp AttackedThisCombatComponent before the TappedEvent, and only during the
        // declare attackers step — so this is the faithful "isn't being declared as an attacker" gate.
        interveningIf = Conditions.Not(
            Conditions.All(
                IsInStep(listOf(Step.DECLARE_ATTACKERS), yoursOnly = false),
                Conditions.EntityMatches(
                    EffectTarget.TriggeringEntity,
                    GameObjectFilter.Any.attackedThisCombat(),
                ),
            ),
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "8"
        artist = "Randy Vargas"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c434e774-c908-46b9-ba18-52e1efe092de.jpg?1783925007"
    }
}
