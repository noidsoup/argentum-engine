package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalManaOnSourceTap
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nirkana Revenant
 * {4}{B}{B}
 * Creature — Vampire Shade
 * 4/4
 *
 * Whenever you tap a Swamp for mana, add an additional {B}.
 * {B}: This creature gets +1/+1 until end of turn.
 *
 * Canonical printing: Rise of the Eldrazi, the card's earliest real printing.
 */
val NirkanaRevenant = card("Nirkana Revenant") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Shade"
    power = 4
    toughness = 4
    oracleText = "Whenever you tap a Swamp for mana, add an additional {B}.\n" +
        "{B}: This creature gets +1/+1 until end of turn."

    staticAbility {
        ability = AdditionalManaOnSourceTap(
            sourceFilter = GameObjectFilter.Land.withSubtype("Swamp").youControl(),
            color = Color.BLACK,
        )
    }

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "120"
        artist = "Igor Kieryluk"
        flavorText = "Hate is an everlasting wellspring from which it is eternally sustained."
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aa7c53cf-d314-4f60-bb5b-cf5068ed9915.jpg?1783941983"
    }
}
