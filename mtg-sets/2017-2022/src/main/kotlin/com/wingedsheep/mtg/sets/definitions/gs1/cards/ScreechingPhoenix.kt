package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Screeching Phoenix — Global Series: Jiang Yanggu & Mu Yanling #30
 * {4}{R}{R} · Creature — Phoenix · 4/4
 *
 * Flying
 * {2}{R}: Creatures you control get +1/+0 until end of turn.
 */
val ScreechingPhoenix = card("Screeching Phoenix") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Phoenix"
    power = 4
    toughness = 4
    oracleText =
        "Flying\n" +
            "{2}{R}: Creatures you control get +1/+0 until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{2}{R}")
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.ModifyStats(1, 0, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "30"
        artist = "Tingting Yeh"
        flavorText = "Its wings are a heavenly inferno that sets the world ablaze."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b9477ba-e198-4085-8733-fd392a5648e7.jpg?1783934626"
    }
}
