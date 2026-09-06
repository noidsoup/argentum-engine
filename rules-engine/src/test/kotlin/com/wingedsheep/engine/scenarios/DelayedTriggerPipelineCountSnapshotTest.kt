package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerTiming
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherUntilMatchEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Synthetic Destiny / The Eagles Are Coming! share one engine rule: a delayed trigger scheduled
 * during spell resolution must not carry a lazy pipeline-scoped `count` — the `EffectContext` that
 * held the collection is gone by the time the trigger fires.
 *
 * Eagles freezes `CreateTokenEffect.count`; Synthetic Destiny needs the same for
 * `GatherUntilMatchEffect.count`.
 */
class DelayedTriggerPipelineCountSnapshotTest : FunSpec({

    val PipelineRevealProbe = card("Pipeline Reveal Delay Probe") {
        manaCost = "{4}{U}{U}"
        typeLine = "Sorcery"
        oracleText = "Exile all creatures you control. At the beginning of the next end step, " +
            "reveal cards from the top of your library until you reveal that many creature cards. " +
            "Put those creature cards onto the battlefield."
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
                                RevealCollectionEffect(from = "allRevealed"),
                                FilterCollectionEffect(
                                    from = "allRevealed",
                                    filter = CollectionFilter.MatchesFilter(GameObjectFilter.Creature),
                                    storeMatching = "creaturesToBattlefield",
                                    storeNonMatching = "rest",
                                ),
                                MoveCollectionEffect(
                                    from = "creaturesToBattlefield",
                                    destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                                ),
                                MoveCollectionEffect(
                                    from = "rest",
                                    destination = CardDestination.ToZone(
                                        Zone.LIBRARY,
                                        placement = ZonePlacement.Bottom,
                                    ),
                                    order = CardOrder.Random,
                                ),
                            )
                        ),
                    )
                )
            }
        }
    }

    val BoardStateRevealProbe = card("Board State Reveal Delay Probe") {
        manaCost = "{1}{U}"
        typeLine = "Sorcery"
        oracleText = "At the beginning of the next end step, reveal until X creature cards, " +
            "where X is the number of creatures you control."
        spell {
            effect = Effects.Composite(
                listOf(
                    CreateDelayedTriggerEffect(
                        step = Step.END,
                        timing = DelayedTriggerTiming.CURRENT_TURN_OR_LATER,
                        effect = GatherUntilMatchEffect(
                            filter = GameObjectFilter.Creature,
                            storeMatch = "matched",
                            storeRevealed = "revealed",
                            count = DynamicAmount.AggregateBattlefield(
                                player = Player.You,
                                filter = GameObjectFilter.Creature,
                            ),
                        ),
                    )
                )
            )
        }
    }

    fun driver(vararg extra: com.wingedsheep.sdk.model.CardDefinition): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + extra.toList())
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun settleStack(d: GameTestDriver) {
        var guard = 0
        while (d.stackSize > 0 && guard++ < 20) d.bothPass()
    }

    fun creatureCountOnBattlefield(d: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId): Int =
        d.getCreatures(player).size

    test("pipeline-scoped GatherUntilMatch count is frozen when the delayed trigger is scheduled") {
        val d = driver(PipelineRevealProbe)
        val you = d.activePlayer!!

        d.putCreatureOnBattlefield(you, "Grizzly Bears")
        d.putCreatureOnBattlefield(you, "Savannah Lions")

        val spell = d.putCardInHand(you, PipelineRevealProbe.name)
        d.giveMana(you, Color.BLUE, 6)
        d.submitSuccess(CastSpell(you, spell))
        settleStack(d)

        d.getExile(you).shouldHaveSize(2)
        d.state.delayedTriggers.shouldHaveSize(1)

        val delayedEffect = d.state.delayedTriggers.single().effect
        val composite = delayedEffect.shouldBeInstanceOf<CompositeEffect>()
        val gather = composite.effects.first().shouldBeInstanceOf<GatherUntilMatchEffect>()
        gather.count shouldBe DynamicAmount.Fixed(2)
    }

    test("board-state GatherUntilMatch count stays lazy in a delayed trigger") {
        val d = driver(BoardStateRevealProbe)
        val you = d.activePlayer!!

        d.putCreatureOnBattlefield(you, "Grizzly Bears")
        d.putCreatureOnBattlefield(you, "Savannah Lions")

        val spell = d.putCardInHand(you, BoardStateRevealProbe.name)
        d.giveMana(you, Color.BLUE, 2)
        d.submitSuccess(CastSpell(you, spell))
        settleStack(d)

        val gather = d.state.delayedTriggers.single().effect.shouldBeInstanceOf<GatherUntilMatchEffect>()
        gather.count.shouldBeInstanceOf<DynamicAmount.AggregateBattlefield>()
    }

    test("end step reveal-until uses the exiled count, not zero") {
        val d = driver(PipelineRevealProbe)
        val you = d.activePlayer!!

        d.putCreatureOnBattlefield(you, "Grizzly Bears")
        d.putCreatureOnBattlefield(you, "Savannah Lions")

        // Library top (last call wins): Forest, Lightning Bolt, Grizzly Bears, Savannah Lions.
        // Reveal until 2 creature cards walks Forest, Bolt, then stops on the two creatures.
        d.putCardOnTopOfLibrary(you, "Savannah Lions")
        d.putCardOnTopOfLibrary(you, "Grizzly Bears")
        d.putCardOnTopOfLibrary(you, "Lightning Bolt")
        d.putCardOnTopOfLibrary(you, "Forest")

        val spell = d.putCardInHand(you, PipelineRevealProbe.name)
        d.giveMana(you, Color.BLUE, 6)
        d.submitSuccess(CastSpell(you, spell))
        settleStack(d)

        creatureCountOnBattlefield(d, you) shouldBe 0

        d.passPriorityUntil(Step.END)
        settleStack(d)

        creatureCountOnBattlefield(d, you) shouldBe 2
        d.getCreatures(you).map { d.state.getEntity(it)?.get<CardComponent>()?.name }.toSet() shouldBe
            setOf("Grizzly Bears", "Savannah Lions")
    }

    test("zero creatures exiled yields zero matches at the end step without error") {
        val d = driver(PipelineRevealProbe)
        val you = d.activePlayer!!

        d.putCardOnTopOfLibrary(you, "Grizzly Bears")
        d.putCardOnTopOfLibrary(you, "Savannah Lions")

        val spell = d.putCardInHand(you, PipelineRevealProbe.name)
        d.giveMana(you, Color.BLUE, 6)
        d.submitSuccess(CastSpell(you, spell))
        settleStack(d)

        gatherCount(d) shouldBe DynamicAmount.Fixed(0)

        d.passPriorityUntil(Step.END)
        settleStack(d)

        creatureCountOnBattlefield(d, you) shouldBe 0
    }
})

private fun gatherCount(d: GameTestDriver): DynamicAmount {
    val composite = d.state.delayedTriggers.single().effect as CompositeEffect
    return (composite.effects.first() as GatherUntilMatchEffect).count
}
