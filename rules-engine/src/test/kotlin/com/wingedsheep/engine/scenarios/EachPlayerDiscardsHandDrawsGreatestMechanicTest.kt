package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsDiscardedEvent
import com.wingedsheep.engine.core.CardsDrawnEvent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Mechanic test for [Patterns.Hand.eachPlayerDiscardsHandDrawsGreatest] — Windfall's symmetric
 * "discard your entire hand, then everyone draws the greatest per-player discard count" shape.
 *
 * Distinct from [Patterns.Hand.eachPlayerDiscardsDraws], which is Flux's per-player "any number,
 * draw that many" with no cross-player maximum.
 */
class EachPlayerDiscardsHandDrawsGreatestMechanicTest : FunSpec({

    val WindfallProbe = card("Windfall Probe") {
        manaCost = "{2}{U}"
        typeLine = "Sorcery"
        oracleText = "Each player discards their hand, then draws cards equal to the greatest " +
            "number of cards a player discarded this way."
        spell {
            effect = Patterns.Hand.eachPlayerDiscardsHandDrawsGreatest()
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + WindfallProbe)
        return driver
    }

    fun fillHand(driver: GameTestDriver, playerId: com.wingedsheep.sdk.model.EntityId, count: Int) {
        repeat(count) {
            driver.putCardInHand(playerId, "Island")
        }
    }

    fun clearHand(driver: GameTestDriver, playerId: com.wingedsheep.sdk.model.EntityId) {
        while (driver.getHandSize(playerId) > 0) {
            driver.moveToGraveyard(driver.getHand(playerId).first())
        }
    }

    test("each player draws the greatest per-player discard count, not their own count") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 40, "Forest" to 40),
            startingLife = 20,
        )

        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        clearHand(driver, active)
        clearHand(driver, opponent)
        fillHand(driver, active, 2)
        fillHand(driver, opponent, 5)

        val spell = driver.putCardInHand(active, "Windfall Probe")
        driver.giveMana(active, Color.BLUE, 3)

        driver.castSpell(active, spell).isSuccess shouldBe true
        driver.bothPass()

        val drawEvents = driver.events.filterIsInstance<CardsDrawnEvent>()
        drawEvents.any { it.playerId == active && it.count == 5 } shouldBe true
        drawEvents.any { it.playerId == opponent && it.count == 5 } shouldBe true

        val discardEvents = driver.events.filterIsInstance<CardsDiscardedEvent>()
        discardEvents.any { it.playerId == active && it.cardIds.size == 2 } shouldBe true
        discardEvents.any { it.playerId == opponent && it.cardIds.size == 5 } shouldBe true

        driver.getHandSize(active) shouldBe 5
        driver.getHandSize(opponent) shouldBe 5
    }

    test("a player with an empty hand still draws the greatest discard count") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 40, "Forest" to 40),
            startingLife = 20,
        )

        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        clearHand(driver, active)
        clearHand(driver, opponent)
        fillHand(driver, active, 4)

        val spell = driver.putCardInHand(active, "Windfall Probe")
        driver.giveMana(active, Color.BLUE, 3)

        driver.castSpell(active, spell).isSuccess shouldBe true
        driver.bothPass()

        driver.getHandSize(opponent) shouldBe 4
        driver.events.filterIsInstance<CardsDrawnEvent>()
            .any { it.playerId == opponent && it.count == 4 } shouldBe true
    }

    test("when every hand is empty everyone draws zero") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Island" to 40, "Forest" to 40),
            startingLife = 20,
        )

        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        clearHand(driver, active)
        clearHand(driver, opponent)

        val spell = driver.putCardInHand(active, "Windfall Probe")
        driver.giveMana(active, Color.BLUE, 3)

        driver.castSpell(active, spell).isSuccess shouldBe true
        driver.bothPass()

        driver.getHandSize(active) shouldBe 0
        driver.getHandSize(opponent) shouldBe 0
    }
})
