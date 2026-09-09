package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Glass-Cast Heart
 * {2}{B}
 * Artifact
 *
 * Whenever one or more Vampires you control attack, create a Blood token.
 * {B}, {T}, Pay 1 life: Create a 1/1 white and black Vampire creature token with lifelink.
 * {B}{B}, {T}, Sacrifice this artifact and thirteen Blood tokens: Each opponent loses 13 life
 * and you gain 13 life.
 *
 * Canonical printing: Innistrad: Crimson Vow Commander (VOC).
 */
val GlassCastHeart = card("Glass-Cast Heart") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "Whenever one or more Vampires you control attack, create a Blood token. " +
        "(It's an artifact with \"{1}, {T}, Discard a card, Sacrifice this token: Draw a card.\")\n" +
        "{B}, {T}, Pay 1 life: Create a 1/1 white and black Vampire creature token with lifelink.\n" +
        "{B}{B}, {T}, Sacrifice this artifact and thirteen Blood tokens: Each opponent loses 13 life " +
        "and you gain 13 life."

    triggeredAbility {
        trigger = Triggers.YouAttackWithFilter(
            GameObjectFilter.Creature.youControl().withSubtype(Subtype.VAMPIRE),
        )
        effect = Effects.CreateBlood(1)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{B}"),
            Costs.Tap,
            Costs.PayLife(1),
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE, Color.BLACK),
            creatureTypes = setOf("Vampire"),
            keywords = setOf(Keyword.LIFELINK),
            imageUri = "https://cards.scryfall.io/normal/front/7/e/7eee78d3-c65f-4454-bd3c-1c55388422f5.jpg?1783924693",
        )
        description = "{B}, {T}, Pay 1 life: Create a 1/1 white and black Vampire creature token with lifelink."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{B}{B}"),
            Costs.Tap,
            Costs.SacrificeSelf,
            Costs.SacrificeMultiple(13, GameObjectFilter.Artifact.withSubtype("Blood")),
        )
        effect = Effects.Composite(
            Effects.LoseLife(13, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(13),
        )
        description = "{B}{B}, {T}, Sacrifice this artifact and thirteen Blood tokens: Each opponent " +
            "loses 13 life and you gain 13 life."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "18"
        artist = "Alayna Danner"
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e649110b-9ede-47d8-9fe4-9ef0dd17b19d.jpg?1783925002"
    }
}
