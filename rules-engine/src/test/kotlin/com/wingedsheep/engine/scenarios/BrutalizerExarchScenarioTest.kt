package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Brutalizer Exarch (NPH #105) — ETB modal: tutor a creature to the top, or tuck a noncreature permanent.
 */
class BrutalizerExarchScenarioTest : ScenarioTestBase() {

    init {
        context("Brutalizer Exarch") {

            test("mode 1 searches for a creature and puts it on top of the library") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Brutalizer Exarch")
                    .withLandsOnBattlefield(1, "Forest", 6)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("cast the Exarch") {
                    game.castSpell(1, "Brutalizer Exarch").error shouldBe null
                    game.resolveStack()
                }

                val modeDecision = game.getPendingDecision()
                withClue("ETB mode choice") { modeDecision shouldNotBe null }
                game.submitDecision(OptionChosenResponse(modeDecision!!.id, 0))
                game.resolveStack()

                val search = game.getPendingDecision().shouldBeInstanceOf<SelectCardsDecision>()
                val bears = search.options.first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                }
                game.submitDecision(CardsSelectedResponse(search.id, listOf(bears)))
                game.resolveStack()

                withClue("Grizzly Bears is on top of the library") {
                    game.state.getLibrary(game.player1Id).first().let { top ->
                        game.state.getEntity(top)?.get<CardComponent>()?.name
                    } shouldBe "Grizzly Bears"
                }
            }

            test("mode 2 puts a target noncreature permanent on the bottom of its owner's library") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Brutalizer Exarch")
                    .withLandsOnBattlefield(1, "Forest", 6)
                    .withCardOnBattlefield(2, "Bonesplitter")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val artifact = game.findPermanent("Bonesplitter")!!

                withClue("cast the Exarch") {
                    game.castSpell(1, "Brutalizer Exarch").error shouldBe null
                    game.resolveStack()
                }

                val modeDecision = game.getPendingDecision()
                game.submitDecision(OptionChosenResponse(modeDecision!!.id, 1))

                val targetDecision = game.getPendingDecision()
                withClue("mode 2 prompts for a noncreature permanent target") {
                    targetDecision shouldNotBe null
                }
                game.submitDecision(TargetsResponse(targetDecision!!.id, mapOf(0 to listOf(artifact))))
                game.resolveStack()

                withClue("artifact left the battlefield") {
                    game.findPermanent("Bonesplitter") shouldBe null
                }
                withClue("artifact is on the bottom of Player2's library") {
                    game.state.getLibrary(game.player2Id).last().let { bottom ->
                        game.state.getEntity(bottom)?.get<CardComponent>()?.name
                    } shouldBe "Bonesplitter"
                }
            }
        }
    }
}
