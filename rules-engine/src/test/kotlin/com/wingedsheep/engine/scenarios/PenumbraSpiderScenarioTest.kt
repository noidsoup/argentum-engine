package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.tsp.cards.PenumbraSpider
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Penumbra Spider — When this creature dies, create a 2/4 black Spider creature token with reach.
 */
class PenumbraSpiderScenarioTest : FunSpec({

    test("creates a 2/4 black Spider token with reach when it dies") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(PenumbraSpider)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30), startingLife = 20)

        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val spider = driver.putCreatureOnBattlefield(player, "Penumbra Spider") // 2/4 green
        val doom = driver.putCardInHand(player, "Doom Blade")
        driver.giveMana(player, Color.BLACK, 1)
        driver.giveColorlessMana(player, 1)
        driver.castSpell(player, doom, targets = listOf(spider))
        driver.bothPass() // Doom Blade → spider dies → trigger on stack
        driver.bothPass() // dies trigger → token

        driver.findPermanent(player, "Penumbra Spider") shouldBe null
        driver.findPermanent(player, "Spider Token") shouldNotBe null
    }
})
