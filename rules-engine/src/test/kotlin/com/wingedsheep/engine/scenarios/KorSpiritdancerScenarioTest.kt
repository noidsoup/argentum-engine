package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Kor Spiritdancer (ROE) — {1}{W} 0/2 Kor Wizard.
 *
 * This creature gets +2/+2 for each Aura attached to it.
 * Whenever you cast an Aura spell, you may draw a card.
 */
class KorSpiritdancerScenarioTest : ScenarioTestBase() {

    init {
        context("Kor Spiritdancer") {
            test("gets +2/+2 for each Aura attached") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kor Spiritdancer")
                    .withCardAttachedTo(1, "Pacifism", "Kor Spiritdancer")
                    .withCardAttachedTo(1, "Pacifism", "Kor Spiritdancer")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dancer = game.findPermanent("Kor Spiritdancer")!!
                val projected = game.state.projectedState
                withClue("two Auras grant +4/+4 on a 0/2") {
                    projected.getPower(dancer) shouldBe 4
                    projected.getToughness(dancer) shouldBe 6
                }
            }

            test("casting an Aura spell may draw a card") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kor Spiritdancer")
                    .withCardInHand(1, "Pacifism")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withCardInLibrary(1, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val handBefore = game.handSize(1)
                val libraryBefore = game.librarySize(1)

                game.castSpell(1, "Pacifism", targetId = bears).error shouldBe null
                game.resolveStack()

                game.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()

                withClue("Pacifism left hand, then may-draw restores hand size") {
                    game.handSize(1) shouldBe handBefore
                    game.librarySize(1) shouldBe libraryBefore - 1
                }
                withClue("Pacifism resolved onto the battlefield") {
                    game.isOnBattlefield("Pacifism") shouldBe true
                }
            }

            test("an opponent casting an Aura does not trigger the draw") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kor Spiritdancer")
                    .withCardInHand(2, "Pacifism")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Plains", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val handBefore = game.handSize(1)
                val libraryBefore = game.librarySize(1)

                game.castSpell(2, "Pacifism", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("only the opponent's Aura cast — no draw for Kor's controller") {
                    game.handSize(1) shouldBe handBefore
                    game.librarySize(1) shouldBe libraryBefore
                }
            }
        }
    }
}
