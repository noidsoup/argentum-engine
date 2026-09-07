package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Spectral Deluge — Kaldheim Commander (KHC) #7
 * {4}{U}{U} · Sorcery
 *
 * Return each creature your opponents control with toughness X or less to its owner's hand,
 * where X is the number of Islands you control.
 * Foretell {1}{U}{U}
 *
 * The toughness cap is [GameObjectFilter.Creature.opponentControls] +
 * [GameObjectFilter.toughnessAtMostDynamic] over a battlefield Island count — not spell {X}.
 */
val SpectralDeluge = card("Spectral Deluge") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Return each creature your opponents control with toughness X or less to its " +
        "owner's hand, where X is the number of Islands you control.\n" +
        "Foretell {1}{U}{U} (During your turn, you may pay {2} and exile this card from your hand " +
        "face down. Cast it on a later turn for its foretell cost.)"

    keywords(Keyword.FORETELL)

    val islandCount =
        DynamicAmounts.battlefield(Player.You, GameObjectFilter.Land.withSubtype("Island")).count()

    spell {
        effect = Patterns.Group.returnAllToHand(
            GroupFilter(
                GameObjectFilter.Creature.opponentControls().toughnessAtMostDynamic(islandCount),
            ),
        )
    }

    keywordAbility(KeywordAbility.foretell("{1}{U}{U}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "7"
        artist = "Jesper Ejsing"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/7238c46e-6338-4aca-96f2-934c44c8cc36.jpg?1783928340"
    }
}
