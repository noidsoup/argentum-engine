package com.wingedsheep.mtg.sets.definitions.c15.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerTiming
import com.wingedsheep.sdk.scripting.effects.GatherUntilMatchEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Synthetic Destiny — Commander 2015 #15 (canonical printing)
 * {4}{U}{U} · Instant
 *
 * Exile all creatures you control. At the beginning of the next end step, reveal cards from the
 * top of your library until you reveal that many creature cards, put all creature cards revealed
 * this way onto the battlefield, then shuffle the rest of the revealed cards into your library.
 *
 * The exile count is frozen when the delayed trigger is scheduled: [moveTracked] records how many
 * creatures actually left the battlefield, and [DynamicAmount.DistinctEntitiesInCollections] over
 * that collection is snapshotted by [CreateDelayedTriggerExecutor] — the pipeline is gone by the
 * next end step (The Eagles Are Coming! / Synthetic Destiny engine rule).
 *
 * "Shuffle the rest into your library" is [MoveCollectionEffect] of the matched creatures to the
 * battlefield, then [ShuffleLibraryEffect] — the non-creature cards revealed this way never left
 * the library and are randomized with the remainder (Polymorph / Thicket Elemental shape).
 */
val SyntheticDestiny = card("Synthetic Destiny") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Exile all creatures you control. At the beginning of the next end step, reveal " +
        "cards from the top of your library until you reveal that many creature cards, put all " +
        "creature cards revealed this way onto the battlefield, then shuffle the rest of the " +
        "revealed cards into your library."

    spell {
        effect = Effects.Pipeline {
            val onBattlefield = gather(
                CardSource.BattlefieldMatching(GameObjectFilter.Creature.youControl()),
                name = "creaturesOnBf",
            )
            val exiled = moveTracked(
                onBattlefield,
                CardDestination.ToZone(Zone.EXILE),
                name = "exiledCreatures",
            )
            run(
                CreateDelayedTriggerEffect(
                    step = Step.END,
                    timing = DelayedTriggerTiming.CURRENT_TURN_OR_LATER,
                    effect = Effects.Composite(
                        listOf(
                            GatherUntilMatchEffect(
                                filter = GameObjectFilter.Creature,
                                storeMatch = "matchedCreatures",
                                storeRevealed = "allRevealed",
                                count = DynamicAmount.DistinctEntitiesInCollections(
                                    listOf(exiled.key)
                                ),
                            ),
                            RevealCollectionEffect(
                                from = "allRevealed",
                                fromZone = Zone.LIBRARY,
                                toZone = Zone.BATTLEFIELD,
                            ),
                            MoveCollectionEffect(
                                from = "matchedCreatures",
                                destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                            ),
                            ShuffleLibraryEffect(),
                        )
                    ),
                )
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "15"
        artist = "Dave Kendall"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6ab025e6-9ee7-45f0-b829-199637eb0038.jpg?1783938115"
    }
}
