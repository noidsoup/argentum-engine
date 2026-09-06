package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Sage of the Beyond — Kaldheim Commander (KHC) #6
 * {5}{U}{U} · Creature — Spirit Giant · 5/5
 *
 * Flying
 * Spells you cast from anywhere other than your hand cost {2} less to cast.
 * Foretell {4}{U}
 *
 * "Anywhere other than your hand" is [SpellCostTarget.YouCastFromZones] over all zones except
 * [Zone.HAND] (Bilbo, Thief in the Night). Only generic mana is reduced; colored requirements
 * are untouched.
 *
 * Foretell is display-only as [Keyword.FORETELL]; cast/exile wiring is [KeywordAbility.foretell]
 * (Stoic Farmer / Cosmic Intervention).
 */
val SageOfTheBeyond = card("Sage of the Beyond") {
    manaCost = "{5}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit Giant"
    power = 5
    toughness = 5
    oracleText = "Flying\n" +
        "Spells you cast from anywhere other than your hand cost {2} less to cast.\n" +
        "Foretell {4}{U} (During your turn, you may pay {2} and exile this card from your hand " +
        "face down. Cast it on a later turn for its foretell cost.)"

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCastFromZones(
                zones = Zone.entries.toSet() - Zone.HAND,
                filter = GameObjectFilter.Any,
            ),
            modification = CostModification.ReduceGeneric(2),
        )
    }

    keywordAbility(KeywordAbility.foretell("{4}{U}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "6"
        artist = "Cristi Balanescu"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f5497d0-0765-4619-a218-b6a8709810cb.jpg?1783928339"
    }
}
