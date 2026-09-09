package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Fell the Mighty (C14 #7) — "Destroy all creatures with power greater than target creature's power."
 */
class FellTheMightyScenarioTest : ScenarioTestBase() {

    init {
        test("destroys only creatures with power strictly greater than the targeted creature") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Force of Nature")
                .withCardOnBattlefield(1, "Hill Giant")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardInHand(1, "Fell the Mighty")
                .withLandsOnBattlefield(1, "Plains", 5)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val hillGiant = game.findPermanent("Hill Giant")!!

            game.castSpell(1, "Fell the Mighty", hillGiant).error shouldBe null
            game.resolveStack()

            withClue("the 5/5 has power greater than the targeted 3/3") {
                game.findPermanent("Force of Nature").shouldBeNull()
            }
            withClue("the targeted 3/3 is not greater than itself") {
                game.findPermanent("Hill Giant").shouldNotBeNull()
            }
            withClue("the 2/2 is not greater than the targeted 3/3") {
                game.findPermanent("Grizzly Bears").shouldNotBeNull()
            }
        }
    }
}
