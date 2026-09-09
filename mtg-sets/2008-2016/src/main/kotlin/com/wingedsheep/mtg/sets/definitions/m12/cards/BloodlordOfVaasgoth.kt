package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.bloodthirst
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.SpellCastEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Bloodlord of Vaasgoth
 * {3}{B}{B}
 * Creature — Vampire Warrior
 * 3/3
 *
 * Bloodthirst 3
 * Flying
 * Whenever you cast a Vampire creature spell, it gains bloodthirst 3.
 */
val BloodlordOfVaasgoth = card("Bloodlord of Vaasgoth") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Warrior"
    power = 3
    toughness = 3
    oracleText = "Bloodthirst 3 (If an opponent was dealt damage this turn, this creature enters " +
        "with three +1/+1 counters on it.)\n" +
        "Flying\n" +
        "Whenever you cast a Vampire creature spell, it gains bloodthirst 3."

    bloodthirst(3)
    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = TriggerSpec(
            SpellCastEvent(
                spellFilter = GameObjectFilter.Creature.withSubtype(Subtype.VAMPIRE),
                player = Player.You,
            ),
            TriggerBinding.ANY,
        )
        effect = Effects.GrantKeywordToSpell(Keyword.BLOODTHIRST, keywordParameter = 3)
        description = "Whenever you cast a Vampire creature spell, it gains bloodthirst 3."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "82"
        artist = "Greg Staples"
        imageUri = "https://cards.scryfall.io/normal/front/1/2/125c5cff-d4e9-4655-9cc5-3ce21e577569.jpg?1783941086"
    }
}
