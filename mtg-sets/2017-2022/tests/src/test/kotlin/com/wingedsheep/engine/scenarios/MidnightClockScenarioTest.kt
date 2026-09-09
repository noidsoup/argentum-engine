package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.eld.cards.MidnightClock
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Midnight Clock (ELD #54 / VOC #108) — hour counters; twelfth triggers reset. */
class MidnightClockScenarioTest : ScenarioTestBase() {

    init {
        context("Midnight Clock") {

            test("the twelfth hour counter shuffles hand and graveyard, draws seven, and exiles the clock") {
                var builder = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Midnight Clock")
                    .withCardInHand(1, "Opt")
                    .withCardInHand(1, "Consider")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(10) { builder = builder.withCardInLibrary(1, "Island") }
                val game = builder.build()

                val clock = game.findPermanent("Midnight Clock")!!
                game.state = game.state.updateEntity(clock) {
                    it.with(CountersComponent(mapOf(CounterType.HOUR to 11)))
                }

                val libraryBefore = game.librarySize(1)
                val handBefore = game.handSize(1)
                val graveyardBefore = game.graveyardSize(1)
                val abilityId = MidnightClock.activatedAbilities[1].id

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = clock,
                        abilityId = abilityId,
                    ),
                )
                withClue("activation should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                repeat(20) {
                    game.resolveStack()
                    if (game.state.stack.isEmpty() && game.state.pendingDecision == null) return@repeat
                }

                withClue("hand and graveyard cards were shuffled into library, then seven were drawn") {
                    game.librarySize(1) shouldBe libraryBefore + handBefore + graveyardBefore - 7
                    game.handSize(1) shouldBe 7
                    game.graveyardSize(1) shouldBe 0
                }
                withClue("Midnight Clock was exiled") {
                    game.findPermanent("Midnight Clock") shouldBe null
                    game.state.getZone(game.player1Id, Zone.EXILE).contains(clock) shouldBe true
                }
            }
        }
    }
}
