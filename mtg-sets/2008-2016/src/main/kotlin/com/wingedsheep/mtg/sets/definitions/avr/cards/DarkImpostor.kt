package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.DonorCards
import com.wingedsheep.sdk.scripting.HasAllActivatedAbilitiesOfCards
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dark Impostor
 * {2}{B}
 * Creature — Vampire Assassin
 * 2/2
 *
 * {4}{B}{B}: Exile target creature and put a +1/+1 counter on this creature.
 * This creature has all activated abilities of all creature cards exiled with it.
 *
 * The activated ability files each exiled creature in this permanent's linked-exile pile
 * ([Effects.ExileLinkedToSource]) and adds a +1/+1 counter to the Impostor. The static ability
 * [HasAllActivatedAbilitiesOfCards] over `LINKED_EXILE` with a creature [Filters.Creature] filter
 * surfaces every activated ability of that pile on the Impostor itself — the Patchwork Crawler /
 * Territory Forge shape.
 */
val DarkImpostor = card("Dark Impostor") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Assassin"
    oracleText = "{4}{B}{B}: Exile target creature and put a +1/+1 counter on this creature.\n" +
        "This creature has all activated abilities of all creature cards exiled with it."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{4}{B}{B}")
        val exiled = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.ExileLinkedToSource(exiled),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
        )
        description = "Exile target creature and put a +1/+1 counter on Dark Impostor."
    }

    staticAbility {
        ability = HasAllActivatedAbilitiesOfCards(
            donors = DonorCards.LINKED_EXILE,
            cardFilter = Filters.Creature,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "92"
        artist = "Johannes Voss"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f5e8815-cda8-407d-847c-968b72c061e8.jpg?1783940704"
        ruling(
            "2012-05-01",
            "Dark Impostor gains only activated abilities. It doesn't gain triggered abilities " +
                "or static abilities."
        )
        ruling(
            "2012-05-01",
            "If an activated ability of a card in exile references the card it's printed on by " +
                "name, treat Dark Impostor's version of that ability as though it referenced " +
                "Dark Impostor by name instead."
        )
        ruling(
            "2012-05-01",
            "Once Dark Impostor leaves the battlefield, it will no longer have the activated " +
                "abilities of the creature cards exiled with it."
        )
    }
}
