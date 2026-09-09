package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Oyobi, Who Split the Heavens (BOK #18) — "Whenever you cast a Spirit or Arcane spell, create a
 * 3/3 white Spirit creature token with flying."
 */
class OyobiWhoSplitTheHeavensScenarioTest : ScenarioTestBase() {

    init {
        context("Oyobi, Who Split the Heavens") {

            test("casting a Spirit spell creates a 3/3 white flying Spirit token") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Oyobi, Who Split the Heavens")
                    .withCardInHand(1, "Lantern Kami")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Lantern Kami").error shouldBe null
                game.resolveStack()

                withClue("a Spirit token is created when a Spirit spell is cast") {
                    game.findPermanents("Spirit Token").size shouldBe 1
                }
                val token = game.findPermanents("Spirit Token").first()
                withClue("the token is a 3/3 with flying") {
                    game.state.projectedState.getPower(token) shouldBe 3
                    game.state.projectedState.getToughness(token) shouldBe 3
                    game.state.projectedState.hasKeyword(token, Keyword.FLYING) shouldBe true
                }
            }

            test("casting an Arcane spell creates the same token") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Oyobi, Who Split the Heavens")
                    .withCardInHand(1, "Lava Spike")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Lava Spike", 2).error shouldBe null
                game.resolveStack()

                withClue("Arcane spells trigger Oyobi the same way Spirit spells do") {
                    game.findPermanents("Spirit Token").size shouldBe 1
                }
            }

            test("casting a spell that is neither Spirit nor Arcane creates no token") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Oyobi, Who Split the Heavens")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                withClue("non-Spirit, non-Arcane spells do not trigger Oyobi") {
                    game.findPermanents("Spirit Token").size shouldBe 0
                }
            }
        }
    }
}
