package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ice.cards.SnowCoveredPlains
import com.wingedsheep.mtg.sets.definitions.khc.cards.StoicFarmer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Stoic Farmer (KHC #5) — ETB searches for a basic Plains; destination depends on the land race.
 */
class StoicFarmerScenarioTest : ScenarioTestBase() {

    private fun isTapped(game: TestGame, id: EntityId): Boolean =
        game.state.getEntity(id)?.has<TappedComponent>() ?: false

    private fun isInZone(game: TestGame, player: Int, zone: Zone, id: EntityId): Boolean {
        val playerId = if (player == 1) game.player1Id else game.player2Id
        return game.state.getZone(playerId, zone).contains(id)
    }

    init {
        test("puts the Plains into hand when land counts are even") {
            val game = scenario()
                .withPlayers("P1", "P2")
                .withCardInHand(1, "Stoic Farmer")
                .withLandsOnBattlefield(1, "Plains", 4)
                .withLandsOnBattlefield(2, "Island", 4)
                .withCardInLibrary(1, "Snow-Covered Plains")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val fetchedPlains = game.findCardsInLibrary(1, "Snow-Covered Plains").single()

            game.castSpell(1, "Stoic Farmer").error shouldBe null
            game.resolveStack()
            game.selectCards(listOf(fetchedPlains)).error shouldBe null
            game.resolveStack()

            withClue("equal land counts — opponent does not control more, so the fetched Plains goes to hand") {
                isInZone(game, 1, Zone.HAND, fetchedPlains) shouldBe true
            }
        }

        test("puts the Plains onto the battlefield tapped when an opponent is ahead on lands") {
            val driver = GameTestDriver()
            driver.registerCards(TestCards.all + listOf(StoicFarmer, SnowCoveredPlains))
            driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val me = driver.activePlayer!!
            val opp = driver.getOpponent(me)

            repeat(2) { driver.putLandOnBattlefield(me, "Plains") }
            repeat(5) { driver.putLandOnBattlefield(opp, "Island") }
            val fetchedPlains = driver.putCardOnTopOfLibrary(me, "Snow-Covered Plains")

            val farmer = driver.putCardInHand(me, "Stoic Farmer")
            driver.giveMana(me, Color.WHITE, 4)
            driver.submit(CastSpell(playerId = me, cardId = farmer))
            driver.bothPass()
            driver.bothPass()

            val search = driver.pendingDecision
            search.shouldBeInstanceOf<SelectCardsDecision>()
            driver.submitCardSelection(me, listOf(fetchedPlains))
            driver.bothPass()

            withClue("opponent controls more lands — the fetched Plains enters tapped on the battlefield") {
                driver.state.getZone(me, Zone.BATTLEFIELD).contains(fetchedPlains) shouldBe true
                driver.isTapped(fetchedPlains) shouldBe true
            }
        }
    }
}
