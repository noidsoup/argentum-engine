package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gs1.cards.FeiyiSnake
import com.wingedsheep.mtg.sets.definitions.gs1.cards.FerociousZheng
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** GS1 Extra batch 05 — Feiyi Snake (reach) and Ferocious Zheng (vanilla 4/4). */
class Gs1ExtraBatch05ScenarioTest : FunSpec({

    fun driver(vararg extras: com.wingedsheep.sdk.model.CardDefinition): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + extras.toList())
        d.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("Feiyi Snake: 2/1 with reach") {
        val d = driver(FeiyiSnake)
        val snake = d.putCreatureOnBattlefield(d.player1, "Feiyi Snake")
        d.state.projectedState.getPower(snake) shouldBe 2
        d.state.projectedState.getToughness(snake) shouldBe 1
        d.state.projectedState.hasKeyword(snake, Keyword.REACH) shouldBe true
    }

    test("Ferocious Zheng: 4/4") {
        val d = driver(FerociousZheng)
        val zheng = d.putCreatureOnBattlefield(d.player1, "Ferocious Zheng")
        d.state.projectedState.getPower(zheng) shouldBe 4
        d.state.projectedState.getToughness(zheng) shouldBe 4
    }
})
