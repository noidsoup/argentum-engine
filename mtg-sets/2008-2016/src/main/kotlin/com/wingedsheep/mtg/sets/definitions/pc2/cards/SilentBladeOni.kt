package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CastFromCollectionWithoutPayingCostEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.LookAtTargetHandEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Silent-Blade Oni
 * {3}{U}{U}{B}{B}
 * Creature — Demon Ninja
 * 6/5
 *
 * Ninjutsu {4}{U}{B}
 * Whenever this creature deals combat damage to a player, look at that player's hand. You may cast
 * a spell from among those cards without paying its mana cost.
 *
 * The look uses [LookAtTargetHandEffect] on [Player.TriggeringPlayer] (the player just dealt
 * combat damage). The free cast is the standard gather → choose-up-to-one →
 * [CastFromCollectionWithoutPayingCostEffect] pipeline over nonland cards in that hand, resolved
 * during the trigger's resolution (ruling 2012-06-01).
 */
val SilentBladeOni = card("Silent-Blade Oni") {
    manaCost = "{3}{U}{U}{B}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Demon Ninja"
    power = 6
    toughness = 5
    oracleText = "Ninjutsu {4}{U}{B} ({4}{U}{B}, Return an unblocked attacker you control to hand: " +
        "Put this card onto the battlefield from your hand tapped and attacking.)\n" +
        "Whenever this creature deals combat damage to a player, look at that player's hand. You " +
        "may cast a spell from among those cards without paying its mana cost."

    ninjutsu("{4}{U}{B}")

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.Composite(
            listOf(
                LookAtTargetHandEffect(EffectTarget.PlayerRef(Player.TriggeringPlayer)),
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        zone = Zone.HAND,
                        player = Player.TriggeringPlayer,
                        filter = GameObjectFilter.Nonland,
                    ),
                    storeAs = "handSpells",
                ),
                SelectFromCollectionEffect(
                    from = "handSpells",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "chosenSpell",
                    selectedLabel = "Cast without paying its mana cost",
                ),
                CastFromCollectionWithoutPayingCostEffect(from = "chosenSpell"),
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "105"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63fb057c-a70f-49e4-ba39-f5ce17b09c7f.jpg?1783940593"

        ruling(
            "2012-06-01",
            "If you cast a permanent spell this way, it will enter under your control when it resolves. " +
                "If you cast an instant or sorcery spell this way, you are the controller of that spell.",
        )
        ruling(
            "2012-06-01",
            "The spell you cast via the triggered ability is cast as part of the resolution of that " +
                "ability. Timing restrictions based on the card's type are ignored.",
        )
        ruling(
            "2012-06-01",
            "If you cast a card without paying its mana cost, you can't choose to cast it for any " +
                "alternative costs, such as evoke or morph costs.",
        )
        ruling("2021-03-19", "The ninjutsu ability can be activated only after blockers have been declared.")
    }
}
