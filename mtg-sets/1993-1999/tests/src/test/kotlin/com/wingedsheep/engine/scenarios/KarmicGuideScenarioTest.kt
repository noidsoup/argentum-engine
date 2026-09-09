package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.ProtectionComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Karmic Guide (ULG #11) — {3}{W}{W} Creature — Angel Spirit 2/2
 *
 * Flying, protection from black
 * Echo {3}{W}{W}
 * When this creature enters, return target creature card from your graveyard to the battlefield.
 */
class KarmicGuideScenarioTest : ScenarioTestBase() {

    init {
        context("Karmic Guide") {

            test("ETB reanimates a creature card from your graveyard onto the battlefield") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Karmic Guide")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Karmic Guide").error shouldBe null
                game.resolveStack()

                val bearsCard = game.findCardsInGraveyard(1, "Grizzly Bears").first()
                game.selectTargets(listOf(bearsCard))
                game.resolveStack()

                withClue("Grizzly Bears is reanimated onto the battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("Karmic Guide resolved onto the battlefield") {
                    game.isOnBattlefield("Karmic Guide") shouldBe true
                }
            }

            test("has protection from black") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Karmic Guide")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val guide = game.findPermanent("Karmic Guide")!!
                val protection = game.state.getEntity(guide)?.get<ProtectionComponent>()
                withClue("protection from black is granted on the permanent") {
                    protection?.colors?.contains(Color.BLACK) shouldBe true
                }
            }
        }
    }
}
