package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Sigil of the Empty Throne
 * {3}{W}{W}
 * Enchantment
 *
 * Whenever you cast an enchantment spell, create a 4/4 white Angel creature token with flying.
 *
 * A cast trigger, not a resolution trigger: it goes on the stack above the enchantment spell that
 * triggered it and so resolves first — and still resolves even if that spell is later countered
 * (ruling 2021-03-19). Sigil isn't on the battlefield while it is itself being cast, so casting it
 * never triggers its own ability.
 */
val SigilOfTheEmptyThrone = card("Sigil of the Empty Throne") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever you cast an enchantment spell, create a 4/4 white Angel creature token " +
        "with flying."

    triggeredAbility {
        trigger = Triggers.YouCastEnchantment
        effect = CreateTokenEffect(
            power = 4,
            toughness = 4,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Angel"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/d/9/d902520e-b4ed-4805-9f64-096acf7c5f31.jpg?1783942460"
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "18"
        artist = "Cyril Van Der Haegen"
        flavorText = "When Asha left Bant, she ensured that the world would have protection and order in her absence."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c451f63-2827-49fe-9d1d-87c87f7f5f8d.jpg?1783942490"

        ruling("2021-03-19", "An ability that triggers when a player casts a spell resolves before the spell that caused it to trigger, but after targets have been chosen for that spell. It resolves even if that spell is countered.")
        ruling("2021-03-19", "Because it's not on the battlefield yet, casting Sigil of the Empty Throne won't cause its own ability to trigger.")
    }
}
