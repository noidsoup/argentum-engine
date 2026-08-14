package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.pc2.cards.MaelstromWanderer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Maelstrom Wanderer (PC2) has cascade twice — two separate cast triggers must each resolve.
 */
class MaelstromWandererScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(MaelstromWanderer)
        return driver
    }

    fun GameTestDriver.fundWanderer(me: EntityId) {
        giveMana(me, Color.GREEN, 1)
        giveMana(me, Color.BLUE, 1)
        giveMana(me, Color.RED, 1)
        giveColorlessMana(me, 5)
    }

    test("cascade, cascade fires two free-cast offers") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        // Two cascade hits: each walks a Forest then finds a Courser (MV 3 < Wanderer's 8).
        driver.putCardOnTopOfLibrary(me, "Centaur Courser")
        driver.putCardOnTopOfLibrary(me, "Forest")
        driver.putCardOnTopOfLibrary(me, "Centaur Courser")
        driver.putCardOnTopOfLibrary(me, "Forest")

        val wanderer = driver.putCardInHand(me, "Maelstrom Wanderer")
        driver.fundWanderer(me)
        driver.submit(
            CastSpell(playerId = me, cardId = wanderer, paymentStrategy = PaymentStrategy.FromPool)
        ).isSuccess shouldBe true

        // First cascade trigger.
        driver.bothPass()
        withClue("first cascade may-cast") {
            driver.isPaused shouldBe true
            driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        }
        driver.submitYesNo(me, true).isSuccess shouldBe true
        driver.bothPass() // first Courser resolves

        // Second cascade trigger.
        driver.bothPass()
        withClue("second cascade may-cast") {
            driver.isPaused shouldBe true
            driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        }
        driver.submitYesNo(me, true).isSuccess shouldBe true
        driver.bothPass() // second Courser
        driver.bothPass() // Wanderer

        withClue("both cascade hits and Wanderer are on the battlefield") {
            driver.getPermanents(me).count { id ->
                driver.state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name ==
                    "Centaur Courser"
            } shouldBe 2
            driver.findPermanent(me, "Maelstrom Wanderer").shouldNotBeNull()
        }
    }
})
