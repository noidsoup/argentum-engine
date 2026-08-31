package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Auratouched Mage
 * {5}{W}
 * Creature — Human Wizard
 * 3/3
 * When this creature enters, search your library for an Aura card that could enchant it. If this
 * creature is still on the battlefield, put that Aura card onto the battlefield attached to it.
 * Otherwise, reveal the Aura card and put it into your hand. Then shuffle.
 */
val AuratouchedMage = card("Auratouched Mage") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Wizard"
    oracleText =
        "When this creature enters, search your library for an Aura card that could enchant it. " +
            "If this creature is still on the battlefield, put that Aura card onto the battlefield attached to it. " +
            "Otherwise, reveal the Aura card and put it into your hand. Then shuffle."
    power = 3
    toughness = 3
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchAuraThatCouldEnchant()
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "1"
        artist = "Jeff Miracola"
        flavorText = "\"I am bound to it and it to me. We came searching together.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/4/942d6414-11e1-4822-9c5d-e4f96846a85f.jpg?1783943709"
    }
}
