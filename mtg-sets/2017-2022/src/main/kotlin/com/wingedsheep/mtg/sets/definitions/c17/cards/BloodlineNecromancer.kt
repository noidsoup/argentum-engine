package com.wingedsheep.mtg.sets.definitions.c17.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Bloodline Necromancer
 * {4}{B}
 * Creature — Vampire Wizard
 * 3/2
 *
 * Lifelink
 * When this creature enters, you may return target Vampire or Wizard creature card from your
 * graveyard to the battlefield.
 *
 * Canonical printing — Commander 2017. VOC and later sets are [Printing] rows only.
 */
val BloodlineNecromancer = card("Bloodline Necromancer") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Wizard"
    power = 3
    toughness = 2
    oracleText = "Lifelink\n" +
        "When this creature enters, you may return target Vampire or Wizard creature card from " +
        "your graveyard to the battlefield."

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val creature = target(
            "target",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Creature
                        .withAnySubtype("Vampire", "Wizard")
                        .ownedByYou(),
                    zone = Zone.GRAVEYARD,
                ),
            ),
        )
        effect = Effects.PutOntoBattlefieldFromGraveyard(creature)
        description = "When this creature enters, you may return target Vampire or Wizard creature " +
            "card from your graveyard to the battlefield."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "14"
        artist = "Joe Slucher"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42bffd03-3821-4b0f-9535-2eb455154587.jpg?1783935946"
    }
}
