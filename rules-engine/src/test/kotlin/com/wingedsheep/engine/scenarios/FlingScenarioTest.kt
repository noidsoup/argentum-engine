package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sth.cards.Fling
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Fling — sacrifice a creature; deal damage equal to its power to any target.
 */
class FlingScenarioTest : FunSpec({

    test("deals damage equal to sacrificed creature's power") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(Fling)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears") // 2/2
        val before = driver.getLifeTotal(opponent)

        val fling = driver.putCardInHand(player, "Fling")
        driver.giveMana(player, Color.RED, 1)
        driver.giveColorlessMana(player, 1)

        val result = driver.submit(
            CastSpell(
                playerId = player,
                cardId = fling,
                targets = listOf(ChosenTarget.Player(opponent)),
                additionalCostPayment = AdditionalCostPayment(sacrificedPermanents = listOf(bears)),
                paymentStrategy = PaymentStrategy.FromPool
            )
        )
        result.isSuccess shouldBe true
        driver.bothPass()

        driver.findPermanent(player, "Grizzly Bears") shouldBe null
        driver.getLifeTotal(opponent) shouldBe before - 2
    }
})
