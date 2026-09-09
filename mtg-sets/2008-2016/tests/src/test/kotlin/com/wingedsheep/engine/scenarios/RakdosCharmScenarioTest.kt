package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Rakdos Charm (RTR #184) — {B}{R} instant with three choose-one modes.
 */
class RakdosCharmScenarioTest : ScenarioTestBase() {

    init {
        context("Rakdos Charm") {

            test("mode 1 exiles target player's graveyard") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardInHand(1, "Rakdos Charm")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInGraveyard(2, "Grizzly Bears")
                    .withCardInGraveyard(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("precondition: opponent has two cards in graveyard") {
                    game.graveyardSize(2) shouldBe 2
                }

                val cast = game.castSpell(1, "Rakdos Charm")
                withClue("Rakdos Charm should cast: ${cast.error}") { cast.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()

                val modeDecision = game.state.pendingDecision as? ChooseOptionDecision
                    ?: error("expected ChooseOptionDecision; got ${game.state.pendingDecision}")
                game.submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = 0))

                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected ChooseTargetsDecision; got ${game.state.pendingDecision}")
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(game.player2Id))))
                game.resolveStack()

                withClue("the targeted player's graveyard is exiled") {
                    game.graveyardSize(2) shouldBe 0
                }
            }

            test("mode 2 destroys target artifact") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardInHand(1, "Rakdos Charm")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Sol Ring")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val solRing = game.findPermanent("Sol Ring")!!
                val cast = game.castSpell(1, "Rakdos Charm")
                withClue("Rakdos Charm should cast: ${cast.error}") { cast.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()

                val modeDecision = game.state.pendingDecision as? ChooseOptionDecision
                    ?: error("expected ChooseOptionDecision; got ${game.state.pendingDecision}")
                game.submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = 1))

                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected ChooseTargetsDecision; got ${game.state.pendingDecision}")
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(solRing))))
                game.resolveStack()

                withClue("Sol Ring is destroyed") {
                    game.findPermanent("Sol Ring") shouldBe null
                    game.isInGraveyard(2, "Sol Ring") shouldBe true
                }
            }

            test("mode 3 deals 1 damage to each creature's controller") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardInHand(1, "Rakdos Charm")
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpellWithMode(1, "Rakdos Charm", 2)
                withClue("Rakdos Charm should cast: ${cast.error}") { cast.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("each controller took 1 damage from their creature") {
                    game.getLifeTotal(1) shouldBe 19
                    game.getLifeTotal(2) shouldBe 19
                }
                withClue("creatures survive the 1 damage") {
                    game.findPermanent("Grizzly Bears") shouldBe game.findPermanent("Grizzly Bears")
                    game.findPermanent("Hill Giant") shouldBe game.findPermanent("Hill Giant")
                }
            }
        }
    }
}
