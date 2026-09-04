package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Aggressive Instinct — GS1 #34 */
class AggressiveInstinctScenarioTest : ScenarioTestBase() {

    init {
        test("your creature deals damage equal to its power to an opponent's creature") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Aggressive Instinct")
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withCardOnBattlefield(2, "Hill Giant")
                .withLandsOnBattlefield(1, "Forest", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val attacker = game.findPermanent("Grizzly Bears")!!
            val defender = game.findPermanent("Hill Giant")!!
            val spell = game.findCardsInHand(1, "Aggressive Instinct").first()

            game.execute(
                CastSpell(
                    game.player1Id,
                    spell,
                    listOf(ChosenTarget.Permanent(attacker), ChosenTarget.Permanent(defender)),
                )
            ).error shouldBe null
            game.resolveStack()

            withClue("2 power from Grizzly Bears becomes 2 damage on Hill Giant") {
                game.state.getEntity(defender)?.get<DamageComponent>()?.amount shouldBe 2
            }
        }
    }
}
