package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Sakashima's Student (PC2 #24) — ninjutsu and optional enter-as-copy with the Ninja subtype rider.
 */
class SakashimasStudentScenarioTest : ScenarioTestBase() {

    init {
        context("Sakashima's Student") {
            fun castWithCopyChoice(game: TestGame, copyTarget: String?) {
                game.castSpell(1, "Sakashima's Student").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                while (game.state.stack.isNotEmpty() && game.getPendingDecision() == null) {
                    game.passPriority()
                }
                val copyDecision = game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                val ids = copyTarget?.let { listOf(game.findPermanent(it)!!) } ?: emptyList()
                game.submitDecision(CardsSelectedResponse(copyDecision.id, ids))
                game.resolveStack()
            }

            test("entering as a copy keeps the copied name and adds Ninja") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Sakashima's Student")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castWithCopyChoice(game, "Grizzly Bears")

                val copy = game.state.getBattlefield(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                }
                val subtypes = game.state.projectedState.getSubtypes(copy)
                withClue("copy keeps Grizzly Bears and gains Ninja (subtypes=$subtypes)") {
                    subtypes.any { it.equals("Bear", ignoreCase = true) }.shouldBeTrue()
                    subtypes.any { it.equals("Ninja", ignoreCase = true) }.shouldBeTrue()
                }
                withClue("printed Student is no longer on the battlefield") {
                    game.findPermanent("Sakashima's Student") shouldBe null
                }
            }

            test("declining the copy enters as the printed 0/0 Human Ninja") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Sakashima's Student")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                castWithCopyChoice(game, copyTarget = null)

                val student = game.findPermanent("Sakashima's Student")!!
                withClue("stays Sakashima's Student at 0/0") {
                    game.state.projectedState.getPower(student) shouldBe 0
                    game.state.projectedState.getToughness(student) shouldBe 0
                }
                val subtypes = game.state.projectedState.getSubtypes(student)
                withClue("keeps Human and Ninja (subtypes=$subtypes)") {
                    subtypes.any { it.equals("Human", ignoreCase = true) }.shouldBeTrue()
                    subtypes.any { it.equals("Ninja", ignoreCase = true) }.shouldBeTrue()
                }
            }
        }
    }
}
