package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Timothar, Baron of Bats (VOC #4) — {4}{B}{B} Legendary Creature — Vampire Noble 4/4
 *
 * Ward—Discard a card.
 * Whenever another nontoken Vampire you control dies, you may pay {1} and exile it. If you do,
 * create a 1/1 black Bat creature token with flying. It gains "When this token deals combat
 * damage to a player, sacrifice it and return the exiled card to the battlefield tapped."
 */
class TimotharBaronOfBatsScenarioTest : ScenarioTestBase() {

    private fun TestGame.resolveMayPayAndStack() {
        var guard = 0
        while (guard++ < 30 && (state.stack.isNotEmpty() || state.pendingDecision != null)) {
            when (val decision = state.pendingDecision) {
                is YesNoDecision -> answerYesNo(true)
                is SelectManaSourcesDecision -> submitManaSourcesAutoPay()
                else -> resolveStack()
            }
        }
    }

    init {
        context("Timothar, Baron of Bats") {

            test("paying {1} when another nontoken Vampire dies exiles it and creates a linked Bat") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Timothar, Baron of Bats")
                    .withCardOnBattlefield(1, "Scion of Opulence")
                    .withCardInHand(1, "Doom Blade")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val vampire = game.findPermanent("Scion of Opulence")!!
                game.castSpell(1, "Doom Blade", vampire).error shouldBe null
                game.resolveStack()
                game.resolveMayPayAndStack()

                withClue("Scion of Opulence is exiled, not left in the graveyard") {
                    game.isInExile(1, "Scion of Opulence") shouldBe true
                    game.isInGraveyard(1, "Scion of Opulence") shouldBe false
                }
                withClue("a flying Bat token was created") {
                    game.findPermanents("Bat Token").size shouldBe 1
                    val bat = game.findPermanent("Bat Token")!!
                    game.state.projectedState.hasKeyword(bat, Keyword.FLYING) shouldBe true
                }
            }

            test("declining the {1} payment leaves the Vampire in the graveyard and makes no Bat") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Timothar, Baron of Bats")
                    .withCardOnBattlefield(1, "Scion of Opulence")
                    .withCardInHand(1, "Doom Blade")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val vampire = game.findPermanent("Scion of Opulence")!!
                game.castSpell(1, "Doom Blade", vampire).error shouldBe null
                game.resolveStack()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("declining the rider keeps the Vampire in the graveyard") {
                    game.isInGraveyard(1, "Scion of Opulence") shouldBe true
                    game.isInExile(1, "Scion of Opulence") shouldBe false
                    game.findPermanents("Bat Token").size shouldBe 0
                }
            }

            test("Timothar's own death does not offer the pay-and-exile rider — OTHER binding") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Timothar, Baron of Bats")
                    .withCardInHand(1, "Murder")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val timothar = game.findPermanent("Timothar, Baron of Bats")!!
                game.castSpell(1, "Murder", timothar).error shouldBe null
                game.resolveStack()
                game.resolveStack()

                withClue("another-only wording — no Bat and no exile prompt") {
                    game.isInGraveyard(1, "Timothar, Baron of Bats") shouldBe true
                    game.isInExile(1, "Timothar, Baron of Bats") shouldBe false
                    game.findPermanents("Bat Token").size shouldBe 0
                    game.hasPendingDecision() shouldBe false
                }
            }

            test("Bat combat damage sacrifices the token and returns the exiled Vampire tapped") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Timothar, Baron of Bats")
                    .withCardOnBattlefield(1, "Scion of Opulence", summoningSickness = false)
                    .withCardInHand(1, "Doom Blade")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Mountain")
                    .withCardInLibrary(2, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val vampire = game.findPermanent("Scion of Opulence")!!
                game.castSpell(1, "Doom Blade", vampire).error shouldBe null
                game.resolveStack()
                game.resolveMayPayAndStack()

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Bat Token" to 2)).error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.resolveMayPayAndStack()

                withClue("the Bat token sacrificed itself after connecting") {
                    game.findPermanents("Bat Token").size shouldBe 0
                }
                val returned = game.findPermanent("Scion of Opulence")
                withClue("Scion of Opulence returned from exile onto the battlefield tapped") {
                    returned shouldNotBe null
                    game.state.getEntity(returned!!)?.has<TappedComponent>() shouldBe true
                    game.isInExile(1, "Scion of Opulence") shouldBe false
                }
            }
        }
    }
}
