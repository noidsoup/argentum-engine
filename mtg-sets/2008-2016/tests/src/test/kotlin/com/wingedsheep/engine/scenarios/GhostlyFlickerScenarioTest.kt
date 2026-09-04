package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Ghostly Flicker (AVR #57) — exile two target artifacts, creatures, and/or lands you control,
 * then return those cards to the battlefield under your control.
 */
class GhostlyFlickerScenarioTest : ScenarioTestBase() {
    init {
        test("blinks two creatures and returns them immediately under your control") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withCardOnBattlefield(1, "Glory Seeker", summoningSickness = false)
                .withCardInHand(1, "Ghostly Flicker")
                .withLandsOnBattlefield(1, "Island", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            val seeker = game.findPermanent("Glory Seeker")!!
            val spell = game.findCardsInHand(1, "Ghostly Flicker").single()

            val cast = game.execute(
                CastSpell(
                    playerId = game.player1Id,
                    cardId = spell,
                    targets = listOf(bears, seeker).map { entityIdToChosenTarget(game.state, it) },
                ),
            )
            withClue("casting with two targets: ${cast.error}") { cast.error shouldBe null }
            game.resolveStack()

            withClue("both creatures return immediately — Ghostly Flicker is not a delayed blink") {
                game.isOnBattlefield("Grizzly Bears") shouldBe true
                game.isOnBattlefield("Glory Seeker") shouldBe true
            }
        }
    }
}
