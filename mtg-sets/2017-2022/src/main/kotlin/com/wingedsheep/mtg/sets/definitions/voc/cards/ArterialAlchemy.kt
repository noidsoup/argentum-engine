package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Arterial Alchemy
 * {2}{R}
 * Enchantment
 *
 * When this enchantment enters, create a Blood token for each opponent you have.
 * Blood tokens you control are Equipment in addition to their other types and have
 * "Equipped creature gets +2/+0" and equip {2}.
 *
 * Canonical printing: Innistrad: Crimson Vow Commander (VOC).
 */
val ArterialAlchemy = card("Arterial Alchemy") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, create a Blood token for each opponent you have. " +
        "(It's an artifact with \"{1}, {T}, Discard a card, Sacrifice this token: Draw a card.\")\n" +
        "Blood tokens you control are Equipment in addition to their other types and have " +
        "\"Equipped creature gets +2/+0\" and equip {2}."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Token.createBloodForEachOpponent()
    }

    for (static in Patterns.Token.grantBloodTokensAsEquipment()) {
        staticAbility { ability = static }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "23"
        artist = "Caio Monteiro"
        imageUri = "https://cards.scryfall.io/normal/front/a/f/afe2dba0-55f4-460c-aa89-3921414e7ea1.jpg?1783925000"
    }
}
