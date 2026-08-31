package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Fiery Fall
 * {5}{R}
 * Instant
 *
 * Fiery Fall deals 5 damage to target creature.
 * Basic landcycling {1}{R} ({1}{R}, Discard this card: Search your library for a basic land card,
 * reveal it, put it into your hand, then shuffle.)
 *
 * Note the damage clause is creature-only — it was never errataed to "any target".
 *
 * Basic landcycling is the typecycling family with a basic-land search filter
 * ([KeywordAbility.basicLandcycling]); `TypecycleCardHandler` pays the cost, discards this card, and
 * runs the library search, so cycling triggers and cycling-prevention (Stabilizer) apply to it just
 * as they do to plain cycling (rulings 2009-02-01). The search is a "may", so the player can decline
 * to find a land even with one available.
 */
val FieryFall = card("Fiery Fall") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Fiery Fall deals 5 damage to target creature.\n" +
        "Basic landcycling {1}{R} ({1}{R}, Discard this card: Search your library for a basic " +
        "land card, reveal it, put it into your hand, then shuffle.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(5, creature)
    }

    keywordAbility(KeywordAbility.basicLandcycling("{1}{R}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "63"
        artist = "Daarken"
        flavorText = "Jund feasts on the unprepared."
        imageUri = "https://cards.scryfall.io/normal/front/6/8/687bb467-b447-4901-8a65-cd91fd3aa15d.jpg?1783942479"

        ruling("2009-02-01", "Unlike the normal cycling ability, basic landcycling doesn't allow you to draw a card. Instead, it lets you search your library for a basic land card. You don't choose the type of basic land card you'll find until you're performing the search. After you choose a basic land card in your library, you reveal it, put it into your hand, then shuffle your library.")
        ruling("2009-02-01", "Basic landcycling is a form of cycling. Any ability that triggers on a card being cycled also triggers on a card being basic landcycled. Any ability that stops a cycling ability from being activated also stops a basic landcycling ability from being activated.")
        ruling("2009-02-01", "Basic landcycling is an activated ability. Effects that interact with activated abilities (such as Stifle or Rings of Brighthearth) will interact with basic landcycling. Effects that interact with spells (such as Remove Soul or Faerie Tauntings) will not.")
        ruling("2009-02-01", "You can choose not to find a basic land card, even if there is one in your library.")
    }
}
