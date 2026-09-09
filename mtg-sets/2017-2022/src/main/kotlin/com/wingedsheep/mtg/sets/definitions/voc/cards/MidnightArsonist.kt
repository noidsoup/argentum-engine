package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Midnight Arsonist
 * {3}{R}
 * Creature — Vampire
 * 3/2
 *
 * When this creature enters, destroy up to X target artifacts without mana abilities, where X is
 * the number of Vampires you control.
 *
 * X is a board-state [DynamicAmount.AggregateBattlefield] count at resolution time, paired with
 * an optional multi-target requirement via `dynamicMaxCount` — the WithoutManaAbilities predicate
 * filter excludes mana rocks while still allowing vanilla artifacts.
 */
val MidnightArsonist = card("Midnight Arsonist") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    oracleText = "When this creature enters, destroy up to X target artifacts without mana " +
        "abilities, where X is the number of Vampires you control."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "up to X target artifacts without mana abilities",
            TargetPermanent(
                optional = true,
                dynamicMaxCount = DynamicAmount.AggregateBattlefield(
                    Player.You,
                    GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE),
                ),
                filter = TargetFilter(GameObjectFilter.Artifact.withoutManaAbilities()),
            ),
        )
        effect = ForEachTargetEffect(
            listOf(Effects.Destroy(EffectTarget.ContextTarget(0))),
        )
        description = "When this creature enters, destroy up to X target artifacts without mana " +
            "abilities, where X is the number of Vampires you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "27"
        artist = "Campbell White"
        flavorText = "Killing the slayers was self-preservation. The fire was just for fun."
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d322638-9775-42f4-b75e-d2e1a08bd75d.jpg?1783924997"
    }
}
