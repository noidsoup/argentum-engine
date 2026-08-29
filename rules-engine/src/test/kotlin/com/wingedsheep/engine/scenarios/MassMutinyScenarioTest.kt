package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Mass Mutiny (PC2 {3}{R}{R} sorcery).
 *
 * For each opponent, gain control of up to one target creature that player controls until end of
 * turn. Untap those creatures. They gain haste until end of turn.
 */
class MassMutinyScenarioTest : ScenarioTestBase() {

    init {
        context("Mass Mutiny") {
            test("steals an opponent creature until end of turn, untaps it, and grants haste") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Mass Mutiny")
                    .withCardOnBattlefield(2, "Grizzly Bears", tapped = true)
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Mass Mutiny", targetId = bear).error shouldBe null
                game.resolveStack()

                val projected = game.state.projectedState
                withClue("caster controls the stolen creature") {
                    projected.getController(bear) shouldBe game.player1Id
                }
                withClue("stolen creature is untapped") {
                    game.state.getEntity(bear)?.has<com.wingedsheep.engine.state.components.battlefield.TappedComponent>() shouldBe false
                }
                withClue("stolen creature gains haste") {
                    projected.hasKeyword(bear, Keyword.HASTE) shouldBe true
                }
            }
        }
    }
}
