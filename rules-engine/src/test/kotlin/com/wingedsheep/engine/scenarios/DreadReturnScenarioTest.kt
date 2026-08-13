package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DreadReturnScenarioTest : ScenarioTestBase() {
    init {
        test("returns a creature card from your graveyard to the battlefield") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardInGraveyard(1, "Grizzly Bears")
                .withCardInHand(1, "Dread Return")
                .withLandsOnBattlefield(1, "Swamp", 4)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpellTargetingGraveyardCard(1, "Dread Return", 1, "Grizzly Bears")
                .error shouldBe null
            game.resolveStack()

            game.findPermanent("Grizzly Bears") shouldNotBe null
            game.isInGraveyard(1, "Grizzly Bears") shouldBe false
        }
    }
}
