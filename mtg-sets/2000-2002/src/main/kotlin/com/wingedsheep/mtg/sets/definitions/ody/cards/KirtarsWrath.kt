package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Kirtar's Wrath
 * {4}{W}{W}
 * Sorcery
 *
 * Destroy all creatures. They can't be regenerated.
 * Threshold — If there are seven or more cards in your graveyard, instead destroy all creatures,
 * then create two 1/1 white Spirit creature tokens with flying. Creatures destroyed this way
 * can't be regenerated.
 *
 * The base wrath is [Effects.DestroyAll] with `noRegenerate = true` (Child of Alara / Supreme
 * Verdict shape). Threshold replaces the whole effect via [ConditionalEffect]: the fulfilled
 * branch destroys, then creates Spirit tokens; the unfulfilled branch is the same destroy alone.
 * "Creatures destroyed this way" is covered by `noRegenerate` on the destroy in both branches.
 */
val KirtarsWrath = card("Kirtar's Wrath") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy all creatures. They can't be regenerated.\n" +
        "Threshold — If there are seven or more cards in your graveyard, instead destroy all " +
        "creatures, then create two 1/1 white Spirit creature tokens with flying. Creatures " +
        "destroyed this way can't be regenerated."

    spell {
        val wrath = Effects.DestroyAll(GameObjectFilter.Creature, noRegenerate = true)
        effect = ConditionalEffect(
            condition = Conditions.CardsInGraveyardAtLeast(7),
            effect = wrath.then(
                Effects.CreateToken(
                    count = 2,
                    power = 1,
                    toughness = 1,
                    colors = setOf(Color.WHITE),
                    creatureTypes = setOf("Spirit"),
                    keywords = setOf(Keyword.FLYING),
                    imageUri = "https://cards.scryfall.io/normal/front/8/3/83497714-97ae-4846-8096-f7f1524f0e09.jpg?1783924702",
                ),
            ),
            elseEffect = wrath,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "28"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5a0c4e6-d50e-42e8-b062-8f6ef5950ab7.jpg?1783945277"
    }
}
