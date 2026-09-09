package com.wingedsheep.mtg.sets.definitions.frf.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.GainControlEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mob Rule
 * {4}{R}{R}
 * Sorcery
 *
 * Choose one —
 * • Gain control of all creatures with power 4 or greater until end of turn. Untap those
 *   creatures. They gain haste until end of turn.
 * • Gain control of all creatures with power 3 or less until end of turn. Untap those creatures.
 *   They gain haste until end of turn.
 */
val MobRule = card("Mob Rule") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Gain control of all creatures with power 4 or greater until end of turn. Untap those " +
        "creatures. They gain haste until end of turn.\n" +
        "• Gain control of all creatures with power 3 or less until end of turn. Untap those " +
        "creatures. They gain haste until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode(
                "Gain control of all creatures with power 4 or greater until end of turn. Untap those " +
                    "creatures. They gain haste until end of turn."
            ) {
                val group = GroupFilter(GameObjectFilter.Creature.powerAtLeast(4))
                effect = Effects.Composite(
                    Effects.ForEachInGroup(group, GainControlEffect(EffectTarget.Self, Duration.EndOfTurn)),
                    Effects.ForEachInGroup(group, TapUntapEffect(EffectTarget.Self, tap = false)),
                    Effects.ForEachInGroup(
                        group,
                        GrantKeywordEffect(Keyword.HASTE, EffectTarget.Self, Duration.EndOfTurn),
                    ),
                )
            }
            mode(
                "Gain control of all creatures with power 3 or less until end of turn. Untap those " +
                    "creatures. They gain haste until end of turn."
            ) {
                val group = GroupFilter(GameObjectFilter.Creature.powerAtMost(3))
                effect = Effects.Composite(
                    Effects.ForEachInGroup(group, GainControlEffect(EffectTarget.Self, Duration.EndOfTurn)),
                    Effects.ForEachInGroup(group, TapUntapEffect(EffectTarget.Self, tap = false)),
                    Effects.ForEachInGroup(
                        group,
                        GrantKeywordEffect(Keyword.HASTE, EffectTarget.Self, Duration.EndOfTurn),
                    ),
                )
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "109"
        artist = "Jakub Kasper"
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1d76845f-c827-4cb4-b2d0-3f2cf5ee0e81.jpg?1783938686"
    }
}
