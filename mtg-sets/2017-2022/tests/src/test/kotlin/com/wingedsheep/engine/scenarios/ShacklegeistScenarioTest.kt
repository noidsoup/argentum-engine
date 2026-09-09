package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.mtg.sets.definitions.m21.cards.Shacklegeist
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/** Shacklegeist (M21 #70 / VOC #112) — flying; blocks only flyers; tap two Spirits. */
class ShacklegeistScenarioTest : ScenarioTestBase() {

    init {
        context("Shacklegeist") {

            test("can block only creatures with flying") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Shacklegeist", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withActivePlayer(2)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Hill Giant" to 1)).error shouldBe null

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                val result = game.declareBlockers(mapOf("Shacklegeist" to listOf("Hill Giant")))
                withClue("blocking a non-flying attacker is illegal: ${result.error}") {
                    result.error shouldNotBe null
                }
            }

            test("tap two Spirits taps an opponent's creature") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Shacklegeist", summoningSickness = false)
                    .withCardOnBattlefield(1, "Timin, Youthful Geist", summoningSickness = false)
                    .withCardOnBattlefield(1, "Spectral Sailor", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val geist = game.findPermanent("Shacklegeist")!!
                val giant = game.findPermanent("Hill Giant")!!
                val abilityId = Shacklegeist.activatedAbilities.single().id

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = geist,
                        abilityId = abilityId,
                        targets = listOf(
                            com.wingedsheep.engine.state.components.stack.ChosenTarget.Permanent(giant),
                        ),
                        costPayment = AdditionalCostPayment(
                            tappedPermanents = listOf(
                                game.findPermanent("Timin, Youthful Geist")!!,
                                game.findPermanent("Spectral Sailor")!!,
                            ),
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
                withClue("Shacklegeist still has flying") {
                    game.state.projectedState.hasKeyword(geist, Keyword.FLYING) shouldBe true
                }
            }
        }
    }
}
