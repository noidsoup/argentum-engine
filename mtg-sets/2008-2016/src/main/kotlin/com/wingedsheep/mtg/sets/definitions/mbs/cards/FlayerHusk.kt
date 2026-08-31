package com.wingedsheep.mtg.sets.definitions.mbs.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.livingWeapon
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Flayer Husk
 * {1}
 * Artifact — Equipment
 *
 * Living weapon (When this Equipment enters, create a 0/0 black Phyrexian Germ creature token,
 * then attach this to it.)
 * Equipped creature gets +1/+1.
 * Equip {2}
 */
val FlayerHusk = card("Flayer Husk") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Living weapon (When this Equipment enters, create a 0/0 black Phyrexian Germ creature token, then attach this to it.)\n" +
        "Equipped creature gets +1/+1.\n" +
        "Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    livingWeapon()

    staticAbility {
        ability = ModifyStats(1, 1, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "107"
        artist = "Igor Kieryluk"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cbd47a02-5a6e-4daa-9877-f65c8639c569.jpg?1783941369"
    }
}
