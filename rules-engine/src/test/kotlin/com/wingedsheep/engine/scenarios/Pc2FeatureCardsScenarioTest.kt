package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.PlanarDieFace
import com.wingedsheep.engine.core.PlanarDieRolledEvent
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.composite.RollPlanarDieExecutor
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.effects.RollPlanarDieEffect
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/** Elderwood Scion (PC2 #88) — you-cast / opponents-cast targeting taxes. */
class ElderwoodScionScenarioTest : ScenarioTestBase() {

    init {
        context("Elderwood Scion — spell cost taxes") {
            test("your spells targeting it cost {2} less; opponents' cost {2} more") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Elderwood Scion")
                    .build()

                val elderwood = game.findPermanent("Elderwood Scion")!!
                val calculator = CostCalculator(cardRegistry)
                val stoke = cardRegistry.requireCard("Stoke the Flames")

                val aliceCost = calculator.calculateEffectiveCost(
                    game.state,
                    stoke,
                    game.player1Id,
                    chosenTargets = listOf(elderwood),
                )
                withClue("Alice targeting her own Elderwood: generic drops from 2 to 0") {
                    aliceCost.genericAmount shouldBe 0
                }

                val bobCost = calculator.calculateEffectiveCost(
                    game.state,
                    stoke,
                    game.player2Id,
                    chosenTargets = listOf(elderwood),
                )
                withClue("Bob targeting Alice's Elderwood: generic rises from 2 to 4") {
                    bobCost.genericAmount shouldBe 4
                }

                val minCost = calculator.calculateMinPossibleCost(
                    game.state,
                    stoke,
                    game.player1Id,
                )
                withClue("min possible cost assumes targeting Elderwood for the discount") {
                    minCost.genericAmount shouldBe 0
                }

                val counterspell = cardRegistry.requireCard("Counterspell")
                val counterMin = calculator.calculateMinPossibleCost(game.state, counterspell, game.player1Id)
                withClue("spells that cannot target Elderwood do not get its optimistic discount") {
                    counterMin.toString() shouldBe ManaCost.parse("{U}{U}").toString()
                }
            }
        }
    }
}

/** Flayer Husk (MBS #107 / PC2 #110) — Living weapon Equipment. */
class FlayerHuskScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Flayer Husk — living weapon") {
            test("ETB creates a 0/0 black Phyrexian Germ token and attaches") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Flayer Husk")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Flayer Husk").error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                val germTokens = game.findPermanents("Phyrexian Germ Token")
                withClue("Living weapon should create exactly one Germ token") {
                    germTokens.size shouldBe 1
                }
                val germ = germTokens.single()
                val tokenCard = game.state.getEntity(germ)!!.get<CardComponent>()!!

                withClue("Germ should be a token") {
                    (game.state.getEntity(germ)!!.get<TokenComponent>() != null) shouldBe true
                }
                withClue("Germ should be black") {
                    tokenCard.colors shouldBe setOf(Color.BLACK)
                }

                val projected = stateProjector.project(game.state)
                withClue("Germ should be 1/1 with Flayer Husk +1/+1") {
                    projected.getPower(germ) shouldBe 1
                    projected.getToughness(germ) shouldBe 1
                }

                val husk = game.findPermanent("Flayer Husk")!!
                game.state.getEntity(husk)?.get<AttachedToComponent>()?.targetId shouldBe germ
                game.state.getEntity(germ)?.get<AttachmentsComponent>()?.attachedIds shouldBe listOf(husk)
            }
        }
    }
}

/** Fractured Powerstone (PC2 #111) — planar die activation. */
class FracturedPowerstoneScenarioTest : ScenarioTestBase() {

    init {
        context("Fractured Powerstone") {
            test("planar die roll emits PlanarDieRolledEvent") {
                val executor = RollPlanarDieExecutor()
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Fractured Powerstone")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val stone = game.findPermanent("Fractured Powerstone")!!
                val result = executor.execute(
                    game.state,
                    RollPlanarDieEffect,
                    EffectContext(
                        controllerId = game.player1Id,
                        sourceId = stone,
                    ),
                )

                val roll = result.events.filterIsInstance<PlanarDieRolledEvent>().single()
                withClue("planar die result should be blank, chaos, or planeswalk") {
                    setOf(PlanarDieFace.BLANK, PlanarDieFace.CHAOS, PlanarDieFace.PLANESWALK) shouldContain roll.result
                }
                roll.playerId shouldBe game.player1Id
            }
        }
    }
}

/** Thromok the Insatiable (PC2 #106) — devour X squared. */
class ThromokTheInsatiableScenarioTest : ScenarioTestBase() {

    init {
        context("Thromok the Insatiable — devour X squared") {
            fun castWithDevour(game: TestGame, sacrifice: List<String>) {
                game.castSpell(1, "Thromok the Insatiable").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                while (game.state.stack.isNotEmpty() && game.getPendingDecision() == null) {
                    game.passPriority()
                }
                val devour = game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                val ids = sacrifice.map { game.findPermanent(it)!! }
                game.submitDecision(CardsSelectedResponse(devour.id, ids))
                game.resolveStack()
            }

            test("devouring two creatures places four +1/+1 counters") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Goblin Guide")
                    .withCardInHand(1, "Thromok the Insatiable")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castWithDevour(game, listOf("Grizzly Bears", "Goblin Guide"))

                val thromok = game.findPermanent("Thromok the Insatiable")!!
                val counters = game.state.getEntity(thromok)?.get<CountersComponent>()
                    ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                withClue("devouring 2 creatures → 2² = 4 counters") {
                    counters shouldBe 4
                }
            }
        }
    }
}
