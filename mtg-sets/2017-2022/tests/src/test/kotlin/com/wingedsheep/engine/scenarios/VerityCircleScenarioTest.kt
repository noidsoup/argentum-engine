package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rna.cards.VerityCircle
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Verity Circle (RNA #58 / VOC #116) — opponent tap draw; tap non-flyers. */
class VerityCircleScenarioTest : ScenarioTestBase() {

    init {
        context("Verity Circle") {

            test("{4}{U} taps a creature without flying") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Verity Circle")
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val circle = game.findPermanent("Verity Circle")!!
                val giant = game.findPermanent("Hill Giant")!!
                val abilityId = VerityCircle.activatedAbilities.single().id

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = circle,
                        abilityId = abilityId,
                        targets = listOf(
                            com.wingedsheep.engine.state.components.stack.ChosenTarget.Permanent(giant),
                        ),
                    ),
                )
                withClue("activation should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                withClue("Hill Giant should be tapped") {
                    game.state.getEntity(giant)!!.has<TappedComponent>() shouldBe true
                }
            }
        }
    }
}
