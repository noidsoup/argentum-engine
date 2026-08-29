package com.wingedsheep.mtg.sets.definitions.jud.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Nullmage Advocate
 * {2}{G}
 * Creature — Insect Druid
 * 2/3
 *
 * {T}: Return two target cards from an opponent's graveyard to their hand. Destroy target artifact
 * or enchantment.
 */
val NullmageAdvocate = card("Nullmage Advocate") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect Druid"
    power = 2
    toughness = 3
    oracleText = "{T}: Return two target cards from an opponent's graveyard to their hand. " +
        "Destroy target artifact or enchantment."

    activatedAbility {
        cost = Costs.Tap
        target(
            "two target cards from an opponent's graveyard",
            TargetObject(
                count = 2,
                filter = TargetFilter(GameObjectFilter.Any.ownedByOpponent(), zone = Zone.GRAVEYARD),
                sameOwner = true,
            ),
        )
        val ae = target("target artifact or enchantment", Targets.ArtifactOrEnchantment)
        effect = Effects.Composite(
            Effects.Move(EffectTarget.ContextTarget(0), Zone.HAND),
            Effects.Move(EffectTarget.ContextTarget(1), Zone.HAND),
            Effects.Destroy(ae),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "126"
        artist = "Darrell Riche"
        flavorText = "\"Our unity unmasks your deceit.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1c29991d-82f2-479d-95ca-5c88e9f3f219.jpg?1783945109"
    }
}
