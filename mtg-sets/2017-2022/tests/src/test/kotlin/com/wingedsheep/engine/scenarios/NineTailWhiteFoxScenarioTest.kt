package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Nine-Tail White Fox — Global Series: Jiang Yanggu & Mu Yanling #8
 * {2}{U} Creature — Fox Spirit, 2/2
 *
 * Whenever this creature deals combat damage to a player, draw a card.
 */
class NineTailWhiteFoxScenarioTest : ScenarioTestBase() {

    init {
        test("draws a card when it deals combat damage to a player") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Nine-Tail White Fox", summoningSickness = false)
                .withCardInLibrary(1, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                .build()

            val handBefore = game.handSize(1)

            game.declareAttackers(mapOf("Nine-Tail White Fox" to 2)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
            game.resolveStack()
            if (game.hasPendingDecision()) {
                game.submitDefaultCombatDamage()
                game.resolveStack()
            }
            game.resolveStack()

            withClue("combat damage to the opponent drew a card") {
                game.handSize(1) shouldBe handBefore + 1
            }
        }
    }
}
