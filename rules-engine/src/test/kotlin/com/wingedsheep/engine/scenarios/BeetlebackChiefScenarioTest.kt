package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.pc2.cards.BeetlebackChief
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Beetleback Chief — When this creature enters, create two 1/1 red Goblin creature tokens.
 */
class BeetlebackChiefScenarioTest : FunSpec({

    fun goblinTokens(driver: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId): Int =
        driver.getPermanents(player).count { id ->
            driver.state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.TokenComponent>() != null &&
                driver.state.projectedState.getSubtypes(id)
                    .any { it.equals("Goblin", ignoreCase = true) }
        }

    test("entering creates two Goblin tokens") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(BeetlebackChief))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val player = driver.activePlayer!!
        val chief = driver.putCardInHand(player, "Beetleback Chief")
        driver.giveMana(player, Color.RED, 2)
        driver.giveColorlessMana(player, 2)
        driver.castSpell(player, chief).error shouldBe null

        var guard = 0
        while (guard++ < 20 && (driver.state.stack.isNotEmpty() || driver.pendingDecision != null)) {
            driver.bothPass()
        }

        driver.findPermanent(player, "Beetleback Chief") shouldNotBe null
        goblinTokens(driver, player) shouldBe 2
    }
})
