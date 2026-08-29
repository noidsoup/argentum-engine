package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Ink-Eyes, Servant of Oni (BOK #71 / PC2 #33) — combat damage may reanimate from the damaged
 * player's graveyard.
 */
class InkEyesServantOfOniScenarioTest : ScenarioTestBase() {

    init {
        fun advanceToDecision(game: TestGame) {
            var guard = 0
            while (!game.hasPendingDecision() && guard++ < 20) {
                if (game.state.priorityPlayerId == null) break
                game.passPriority()
            }
        }

        test("combat damage may put a creature from the damaged player's graveyard onto the battlefield") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Ink-Eyes, Servant of Oni")
                .withCardInGraveyard(2, "Hill Giant")
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(2, "Swamp")
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                .build()

            game.declareAttackers(mapOf("Ink-Eyes, Servant of Oni" to 2)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
            game.declareNoBlockers().error shouldBe null
            advanceToDecision(game)

            val giant = game.findCardsInGraveyard(2, "Hill Giant").first()
            game.selectTargets(listOf(giant)).error shouldBe null
            game.resolveStack()

            val reanimated = game.findPermanent("Hill Giant")
            withClue("Hill Giant is on the battlefield under player 1") {
                reanimated shouldNotBe null
                game.state.getEntity(reanimated!!)?.get<CardComponent>()!!.name shouldBe "Hill Giant"
                game.state.projectedState.getController(reanimated) shouldBe game.player1Id
            }
            withClue("left the opponent's graveyard") {
                game.graveyardSize(2) shouldBe 0
            }
        }
    }
}
