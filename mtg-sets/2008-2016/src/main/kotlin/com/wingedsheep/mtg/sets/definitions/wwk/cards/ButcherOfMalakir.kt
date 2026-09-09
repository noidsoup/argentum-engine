package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Butcher of Malakir
 * {5}{B}{B}
 * Creature — Vampire Warrior
 * 5/4
 *
 * Flying
 * Whenever this creature or another creature you control dies, each opponent sacrifices a
 * creature of their choice.
 *
 * Canonical printing: Worldwake (the card's earliest real printing). Later sets get a
 * [com.wingedsheep.sdk.model.Printing] row only.
 *
 * "This creature or another creature you control dies" is [Triggers.YourCreatureDies] — the
 * ANY binding over creatures you control already admits the source. The payoff is the edict
 * shape from Rot-Tide Gargantua: [Effects.Sacrifice] aimed at [Player.EachOpponent].
 */
val ButcherOfMalakir = card("Butcher of Malakir") {
    manaCost = "{5}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Warrior"
    oracleText = "Flying\n" +
        "Whenever this creature or another creature you control dies, each opponent sacrifices a " +
        "creature of their choice."
    power = 5
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YourCreatureDies
        effect = Effects.Sacrifice(
            GameObjectFilter.Creature,
            target = EffectTarget.PlayerRef(Player.EachOpponent),
        )
        description = "Whenever this creature or another creature you control dies, each opponent " +
            "sacrifices a creature of their choice."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "53"
        artist = "Jason Chan"
        flavorText = "His verdict is always guilty. His sentence is always death."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73131341-0fde-4eca-aefa-ce69c933af07.jpg?1783942056"
    }
}
