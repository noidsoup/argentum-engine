package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.dis.cards.CelestialAncient
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Celestial Ancient (DIS #7) — {3}{W}{W} Creature — Elemental 3/3.
 *
 * Flying. Whenever you cast an enchantment spell, put a +1/+1 counter on each creature you control.
 */
class CelestialAncientScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    private val testAura = card("Test Pump Aura") {
        manaCost = "{W}"
        colorIdentity = "W"
        typeLine = "Enchantment — Aura"
        oracleText = "Enchant creature"
        auraTarget = Targets.Creature
        metadata { rarity = Rarity.COMMON; collectorNumber = "1" }
    }

    init {
        cardRegistry.register(CelestialAncient)
        cardRegistry.register(testAura)

        context("Celestial Ancient") {

            test("casting an enchantment puts a +1/+1 counter on each creature you control") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Celestial Ancient")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Test Pump Aura")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val ancient = game.findPermanent("Celestial Ancient")!!

                game.castSpell(1, "Test Pump Aura", bears).error shouldBe null
                game.resolveStack() // aura cast
                game.resolveStack() // enchantment-cast trigger

                withClue("Grizzly Bears gets a +1/+1 counter") {
                    game.state.getEntity(bears)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                    projector.getProjectedPower(game.state, bears) shouldBe 3
                }
                withClue("Celestial Ancient gets a +1/+1 counter") {
                    game.state.getEntity(ancient)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                    projector.getProjectedPower(game.state, ancient) shouldBe 4
                }
            }
        }
    }
}
