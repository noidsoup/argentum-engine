package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.lrw.cards.VividCreek
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Mark of Mutiny (ZEN {2}{R} sorcery).
 *
 * Gain control of target creature until end of turn. Put a +1/+1 counter on it and untap it.
 * That creature gains haste until end of turn.
 */
class MarkOfMutinyScenarioTest : ScenarioTestBase() {

    init {
        context("Mark of Mutiny") {
            test("gains control, adds a +1/+1 counter, untaps, and grants haste") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Mark of Mutiny")
                    .withCardOnBattlefield(2, "Grizzly Bears", tapped = true)
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Grizzly Bears")!!
                game.state.getEntity(bear)?.has<TappedComponent>() shouldBe true

                game.castSpell(1, "Mark of Mutiny", targetId = bear).error shouldBe null
                game.resolveStack()

                val projected = game.state.projectedState
                withClue("caster controls the borrowed creature") {
                    projected.getController(bear) shouldBe game.player1Id
                }
                withClue("creature is untapped") {
                    game.state.getEntity(bear)?.has<TappedComponent>() shouldBe false
                }
                withClue("creature gains haste") {
                    projected.hasKeyword(bear, Keyword.HASTE) shouldBe true
                }
                withClue("creature gets a +1/+1 counter") {
                    val counters = game.state.getEntity(bear)?.get<CountersComponent>()
                    counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                    projected.getPower(bear) shouldBe 3
                    projected.getToughness(bear) shouldBe 3
                }
            }
        }
    }
}
