package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ForetellCard
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.khc.cards.RanarTheEverWatchful
import com.wingedsheep.mtg.sets.definitions.khc.cards.StoicFarmer
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Ranar the Ever-Watchful (KHC #2) — foretell setup discount and exile-triggered Spirit tokens.
 */
class RanarTheEverWatchfulScenarioTest : ScenarioTestBase() {

    init {
        context("Ranar the Ever-Watchful") {

            test("the first foretell each turn costs {0} while Ranar is on the battlefield") {
                val driver = GameTestDriver()
                driver.registerCards(TestCards.all + listOf(RanarTheEverWatchful, StoicFarmer))
                driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
                driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

                val me = driver.activePlayer!!
                driver.putCreatureOnBattlefield(me, "Ranar the Ever-Watchful")
                val farmer = driver.putCardInHand(me, "Stoic Farmer")

                driver.submitSuccess(ForetellCard(me, farmer))

                withClue("foretell succeeded with no mana paid") {
                    driver.getExile(me).contains(farmer) shouldBe true
                    driver.state.foretellCountThisTurnByPlayer[me] shouldBe 1
                }
            }

            test("exiling an opponent's creature with your spell creates one Spirit token") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Ranar the Ever-Watchful")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Swords to Plowshares")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Swords to Plowshares", bear).error shouldBe null
                game.resolveStack()

                withClue("battlefield exile you control makes one Spirit") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.findPermanents("Spirit Token").size shouldBe 1
                }
            }

            test("Spirit tokens have flying") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Ranar the Ever-Watchful")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardInHand(1, "Swords to Plowshares")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Swords to Plowshares", game.findPermanent("Grizzly Bears")!!).error shouldBe null
                game.resolveStack()

                val spirit = game.findPermanents("Spirit Token").single()
                withClue("the token is a 1/1 white Spirit with flying") {
                    game.state.projectedState.hasKeyword(spirit, Keyword.FLYING) shouldBe true
                }
            }
        }
    }
}
