package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Higure, the Still Wind (BOK #37) — {3}{U}{U} 3/4 Legendary Human Ninja.
 *
 * Ninjutsu {2}{U}{U}
 * Whenever Higure deals combat damage to a player, you may search your library for a Ninja card,
 * reveal it, put it into your hand, then shuffle.
 * {2}: Target Ninja creature can't be blocked this turn.
 */
class HigureTheStillWindScenarioTest : ScenarioTestBase() {

    private val unblockAbilityId by lazy {
        cardRegistry.getCard("Higure, the Still Wind")!!.activatedAbilities.first().id
    }

    init {
        context("Higure, the Still Wind") {

            test("combat damage may search for a Ninja card and put it into hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Higure, the Still Wind", summoningSickness = false)
                    .withCardInLibrary(1, "Ninja of the Deep Hours")
                    .withCardInLibrary(1, "Island")
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Higure, the Still Wind" to 2))
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers().error shouldBe null
                var guard = 0
                while (!game.hasPendingDecision() && guard++ < 20) {
                    if (game.state.priorityPlayerId == null) break
                    game.passPriority()
                }

                withClue("the combat-damage trigger is optional — answer yes first") {
                    game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                }
                game.answerYesNo(true).error shouldBe null

                val decision = game.getPendingDecision()
                withClue("the optional Ninja search should offer library selection") {
                    (decision is SelectCardsDecision) shouldBe true
                }
                decision as SelectCardsDecision
                withClue("only the Ninja card is legal — not the basic Island") {
                    decision.options.size shouldBe 1
                }

                game.selectCards(listOf(decision.options.first()))
                game.resolveStack()

                withClue("the fetched Ninja is in hand") {
                    game.findCardsInHand(1, "Ninja of the Deep Hours").size shouldBe 1
                }
            }

            test("{2} grants can't be blocked to a target Ninja creature") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Higure, the Still Wind")
                    .withCardOnBattlefield(1, "Ninja of the Deep Hours", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ninja = game.findPermanent("Ninja of the Deep Hours")!!
                game.state.projectedState.hasKeyword(ninja, AbilityFlag.CANT_BE_BLOCKED) shouldBe false

                game.state = game.state.updateEntity(game.player1Id) { container ->
                    container.with(ManaPoolComponent(colorless = 2))
                }

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = game.findPermanent("Higure, the Still Wind")!!,
                        abilityId = unblockAbilityId,
                        targets = listOf(ChosenTarget.Permanent(ninja)),
                    ),
                )
                withClue("activating unblock should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()

                withClue("the targeted Ninja gains can't be blocked this turn") {
                    game.state.projectedState.hasKeyword(ninja, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
                }
            }
        }
    }
}
