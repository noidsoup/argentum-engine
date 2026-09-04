package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Jagged-Scar Archers — Lorwyn #222
 * {1}{G}{G} · Creature — Elf Archer · star/star P/T
 *
 * Jagged-Scar Archers's power and toughness are each equal to the number of Elves you control.
 * {T}: This creature deals damage equal to its power to target creature with flying.
 *
 * The P/T tally is [Player.You]-scoped Elves on the battlefield — the same shape as Shaman of
 * the Pack's life-loss X, not Heedless One's all-players count. The tap ability reads this
 * creature's power at resolution via [EntityReference.Source].
 */
val JaggedScarArchers = card("Jagged-Scar Archers") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Archer"
    oracleText = "Jagged-Scar Archers's power and toughness are each equal to the number of Elves you control.\n{T}: This creature deals damage equal to its power to target creature with flying."

    dynamicStats(
        DynamicAmount.AggregateBattlefield(
            player = Player.You,
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.ELF),
        ),
    )

    activatedAbility {
        cost = Costs.Tap
        val flyingCreature = target(
            "target creature with flying",
            TargetCreature(filter = TargetFilter.Creature.withKeyword(Keyword.FLYING)),
        )
        effect = Effects.DealDamage(
            amount = DynamicAmount.EntityProperty(EntityReference.Source, EntityNumericProperty.Power),
            target = flyingCreature,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75fd5232-2dac-4bd9-a1f6-eb1a40154367.jpg?1783942861"
    }
}
