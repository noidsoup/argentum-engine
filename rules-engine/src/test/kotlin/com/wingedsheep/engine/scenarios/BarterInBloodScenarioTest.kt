package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.BarterInBlood
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BarterInBloodScenarioTest : FunSpec({

    fun GameTestDriver.drainStack(maxIterations: Int = 40) {
        var guard = 0
        while (guard++ < maxIterations) {
            val pd = pendingDecision
            when {
                pd is SelectCardsDecision ->
                    submitCardSelection(pd.playerId, pd.options.take(pd.minSelections))
                state.stack.isNotEmpty() -> bothPass()
                else -> return
            }
        }
    }

    test("each player sacrifices two creatures") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(BarterInBlood)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.player1
        val opp = driver.player2
        repeat(3) { driver.putCreatureOnBattlefield(me, "Grizzly Bears") }
        repeat(3) { driver.putCreatureOnBattlefield(opp, "Grizzly Bears") }

        val card = driver.putCardInHand(me, "Barter in Blood")
        driver.giveColorlessMana(me, 2)
        driver.giveMana(me, Color.BLACK, 2)
        driver.castSpell(me, card).isSuccess shouldBe true
        driver.drainStack()

        // 3 bears each → sacrifice 2 → 1 left each.
        driver.getCreatures(me).size shouldBe 1
        driver.getCreatures(opp).size shouldBe 1
    }
})
