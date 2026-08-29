package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.pls.cards.SunkenHope
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Sunken Hope (PLS) — {3}{U}{U} Enchantment.
 *
 * At the beginning of each player's upkeep, that player returns a creature they control to its
 * owner's hand.
 */
class SunkenHopeScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SunkenHope))
        return driver
    }

    fun advanceToUpkeepOf(driver: GameTestDriver, player: EntityId) {
        driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        if (driver.activePlayer != player) {
            driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
            driver.passPriorityUntil(Step.UPKEEP, maxPasses = 200)
        }
        driver.currentStep shouldBe Step.UPKEEP
        driver.activePlayer shouldBe player
    }

    test("its controller returns a creature to hand on their upkeep") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))

        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(controller, "Sunken Hope")
        val bear = driver.putCreatureOnBattlefield(controller, "Grizzly Bears")

        advanceToUpkeepOf(driver, controller)
        driver.stackSize shouldBe 1
        driver.bothPass()

        driver.findPermanent(controller, "Grizzly Bears") shouldBe null
        driver.getHand(controller).contains(bear) shouldBe true
    }

    test("with two creatures the controller chooses which to return") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))

        val controller = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(controller, "Sunken Hope")
        val bear1 = driver.putCreatureOnBattlefield(controller, "Grizzly Bears")
        val bear2 = driver.putCreatureOnBattlefield(controller, "Grizzly Bears")

        advanceToUpkeepOf(driver, controller)
        driver.stackSize shouldBe 1
        driver.bothPass()

        driver.state.pendingDecision shouldNotBe null
        driver.submitCardSelection(controller, listOf(bear2))

        driver.getHand(controller).contains(bear2) shouldBe true
        driver.findPermanent(controller, "Grizzly Bears") shouldBe bear1
    }

    test("each opponent also returns a creature on their upkeep") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))

        val controller = driver.activePlayer!!
        val opponent = driver.getOpponent(controller)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putPermanentOnBattlefield(controller, "Sunken Hope")
        val opponentBear = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        advanceToUpkeepOf(driver, opponent)
        driver.stackSize shouldBe 1
        driver.bothPass()

        driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
        driver.getHand(opponent).contains(opponentBear) shouldBe true
    }
})
