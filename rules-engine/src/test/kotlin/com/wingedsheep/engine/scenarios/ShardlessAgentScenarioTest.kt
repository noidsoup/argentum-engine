package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.pc2.cards.ShardlessAgent
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Shardless Agent (PC2 #104) — {1}{G}{U} 2/2 Artifact Creature with cascade.
 *
 * Cascade (CR 702.85a) exiles from the top until a nonland card with lesser mana value appears.
 * Shardless Agent's mana value is 3.
 */
class ShardlessAgentScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(ShardlessAgent)
        return driver
    }

    fun GameTestDriver.fundAgent(me: EntityId) {
        giveColorlessMana(me, 1)
        giveMana(me, Color.GREEN, 1)
        giveMana(me, Color.BLUE, 1)
    }

    test("the printed card carries cascade") {
        ShardlessAgent.keywords.contains(Keyword.CASCADE) shouldBe true
    }

    test("cascade finds a cheaper nonland card and casting it for free resolves it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        driver.putCardOnTopOfLibrary(me, "Grizzly Bears")
        driver.putCardOnTopOfLibrary(me, "Forest")

        val agent = driver.putCardInHand(me, "Shardless Agent")
        driver.fundAgent(me)
        driver.submit(CastSpell(playerId = me, cardId = agent, paymentStrategy = PaymentStrategy.FromPool))
            .isSuccess shouldBe true

        driver.bothPass()
        withClue("cascade should pause with a may-cast decision") {
            driver.isPaused shouldBe true
            driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        }

        driver.submitYesNo(me, true).isSuccess shouldBe true
        driver.bothPass()
        driver.bothPass()

        withClue("the cascaded creature hit the battlefield without being paid for") {
            driver.findPermanent(me, "Grizzly Bears").shouldNotBeNull()
        }
        withClue("Shardless Agent resolves too") {
            driver.findPermanent(me, "Shardless Agent").shouldNotBeNull()
        }
    }
})
