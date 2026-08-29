package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Three Dreams (RAV #32 / PC2 #13) — tutor up to three differently named Auras.
 */
class ThreeDreamsScenarioTest : ScenarioTestBase() {

    init {
        test("finds up to three Aura cards with different names") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Three Dreams")
                .withLandsOnBattlefield(1, "Plains", 5)
                .withCardInLibrary(1, "Pacifism")
                .withCardInLibrary(1, "Grievous Wound")
                .withCardInLibrary(1, "Sandskin")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Three Dreams").error shouldBe null
            game.resolveStack()

            withClue("search selection is offered") { game.hasPendingDecision() shouldBe true }

            val pacifism = game.findCardsInLibrary(1, "Pacifism").first()
            val wound = game.findCardsInLibrary(1, "Grievous Wound").first()
            val sandskin = game.findCardsInLibrary(1, "Sandskin").first()
            game.selectCards(listOf(pacifism, wound, sandskin)).error shouldBe null
            game.resolveStack()

            withClue("all three Auras are in hand") {
                game.handSize(1) shouldBe 3
                game.isInHand(1, "Pacifism") shouldBe true
                game.isInHand(1, "Grievous Wound") shouldBe true
                game.isInHand(1, "Sandskin") shouldBe true
            }
            withClue("library was shuffled") {
                game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY)).size shouldBe 0
            }
        }
    }
}
