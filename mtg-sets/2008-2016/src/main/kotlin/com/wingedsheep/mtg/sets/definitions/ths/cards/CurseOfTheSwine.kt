package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Curse of the Swine
 * {X}{U}{U}
 * Sorcery
 *
 * Exile X target creatures. For each creature exiled this way, its controller creates a 2/2 green
 * Boar creature token.
 */
val CurseOfTheSwine = card("Curse of the Swine") {
    manaCost = "{X}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Exile X target creatures. For each creature exiled this way, its controller " +
        "creates a 2/2 green Boar creature token."

    spell {
        target = TargetCreature(optional = true, dynamicMaxCount = DynamicAmount.XValue)
        effect = ForEachTargetEffect(
            listOf(
                Patterns.Exile.exileAndReplaceWithToken(
                    target = EffectTarget.ContextTarget(0),
                    power = 2,
                    toughness = 2,
                    colors = setOf(Color.GREEN),
                    creatureTypes = setOf("Boar"),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "46"
        artist = "James Ryman"
        flavorText = "Another imminent battle subsided in busy snuffling and carefree rooting."
        imageUri = "https://cards.scryfall.io/normal/front/7/8/78831fc6-ea90-4546-b46a-9c192dbefc65.jpg?1783939800"
    }
}
