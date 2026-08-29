package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Okiba-Gang Shinobi (BOK #76 / PC2 #35) — ninjutsu and a combat-damage discard trigger.
 */
class OkibaGangShinobiScenarioTest : ScenarioTestBase() {

    init {
        test("combat damage makes the defending player discard two cards") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Okiba-Gang Shinobi")
                .withCardInHand(2, "Grizzly Bears")
                .withCardInHand(2, "Hill Giant")
                .withCardInHand(2, "Lightning Bolt")
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(2, "Swamp")
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                .build()

            game.declareAttackers(mapOf("Okiba-Gang Shinobi" to 2)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
            game.resolveStack()

            withClue("defender discarded down to one card") {
                game.handSize(2) shouldBe 1
            }
        }
    }
}
