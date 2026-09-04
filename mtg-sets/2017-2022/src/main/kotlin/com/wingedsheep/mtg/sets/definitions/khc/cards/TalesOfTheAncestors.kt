package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Tales of the Ancestors — Kaldheim Commander (KHC) #8
 * {3}{U} · Sorcery
 *
 * Each player with fewer cards in hand than the player with the most cards in hand draws cards
 * equal to the difference.
 * Foretell {1}{U}
 *
 * The catch-up draw is the No Witnesses / Outpace Oblivion shape: one [ForEachPlayerEffect] over
 * [Player.Each] whose body is a [ConditionalEffect]. Inside the loop the controller is rebound to
 * the iterated player, so hand counts and draws land on the right player.
 *
 * "Fewer than the player with the most" is strictly less than the table maximum — players tied for
 * most do not draw. The maximum is [DynamicAmount.GreatestAmongPlayers] over per-player hand
 * counts, not a table total.
 */
private val mostCardsInHandAmongPlayers = DynamicAmount.GreatestAmongPlayers(
    players = Player.Each,
    inner = DynamicAmount.Count(Player.You, Zone.HAND),
)

val TalesOfTheAncestors = card("Tales of the Ancestors") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Each player with fewer cards in hand than the player with the most cards in hand " +
        "draws cards equal to the difference.\n" +
        "Foretell {1}{U} (During your turn, you may pay {2} and exile this card from your hand " +
        "face down. Cast it on a later turn for its foretell cost.)"

    spell {
        effect = ForEachPlayerEffect(
            players = Player.Each,
            effects = listOf(
                ConditionalEffect(
                    condition = Conditions.CompareAmounts(
                        left = DynamicAmount.Count(Player.You, Zone.HAND),
                        operator = ComparisonOperator.LT,
                        right = mostCardsInHandAmongPlayers,
                    ),
                    effect = Effects.DrawCards(
                        DynamicAmount.IfPositive(
                            DynamicAmount.Subtract(
                                mostCardsInHandAmongPlayers,
                                DynamicAmount.Count(Player.You, Zone.HAND),
                            ),
                        ),
                    ),
                ),
            ),
        )
    }

    keywordAbility(KeywordAbility.foretell("{1}{U}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "8"
        artist = "Colin Boyer"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c286a74c-3714-4190-8322-84b161debe39.jpg?1783928338"
    }
}
