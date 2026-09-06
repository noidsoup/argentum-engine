package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Cultivator of Blades — Kaladesh (KLD) #151
 * {3}{G}{G} · Creature — Elf Artificer · 1/1
 *
 * Fabricate 2 (When this creature enters, put two +1/+1 counters on it or create two 1/1
 * colorless Servo artifact creature tokens.)
 * Whenever this creature attacks, you may have other attacking creatures get +X/+X until end of
 * turn, where X is this creature's power.
 *
 * Fabricate is [KeywordAbility.fabricate] only — the engine derives the ETB modal from the
 * keyword (Weaponcraft Enthusiast / Propeller Pioneer shape).
 *
 * The attack pump is optional ([MayEffect]) and scopes to every *other* attacking creature
 * ([GroupFilter] over [GameObjectFilter.Creature.attacking] with `excludeSelf = true`). X is
 * read from projected source power at resolution via [DynamicAmounts.sourcePower].
 */
val CultivatorOfBlades = card("Cultivator of Blades") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Artificer"
    power = 1
    toughness = 1
    oracleText = "Fabricate 2 (When this creature enters, put two +1/+1 counters on it or create " +
        "two 1/1 colorless Servo artifact creature tokens.)\n" +
        "Whenever this creature attacks, you may have other attacking creatures get +X/+X until " +
        "end of turn, where X is this creature's power."

    keywordAbility(KeywordAbility.fabricate(2))

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = MayEffect(
            Patterns.Group.modifyStatsForAll(
                power = DynamicAmounts.sourcePower(),
                toughness = DynamicAmounts.sourcePower(),
                filter = GroupFilter(
                    baseFilter = GameObjectFilter.Creature.attacking(),
                    excludeSelf = true,
                ),
            ),
        )
        description = "Whenever this creature attacks, you may have other attacking creatures get " +
            "+X/+X until end of turn, where X is this creature's power."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "151"
        artist = "Bastien L. Deharme"
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d21d35b-1b4e-43aa-8fb7-0dd7a2fa91a1.jpg?1783937180"
    }
}
