package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.pc2.cards.EtheriumHornSorcerer
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Etherium-Horn Sorcerer (PC2 #91) — {4}{U}{R} 3/6 Artifact Creature with cascade and
 * "{1}{U}{R}: Return this creature to its owner's hand."
 *
 * The two halves feed each other: bouncing it back means you can recast it and cascade again, so
 * these tests cover the cascade hit at MV 6 and the self-bounce that sets up the loop.
 */
class EtheriumHornSorcererScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(EtheriumHornSorcerer)
        return driver
    }

    test("the printed card carries cascade and a single activated ability") {
        withClue("Etherium-Horn Sorcerer is a cascader with one activated ability") {
            EtheriumHornSorcerer.keywords.contains(Keyword.CASCADE) shouldBe true
            EtheriumHornSorcerer.activatedAbilities.size shouldBe 1
        }
    }

    test("cascade finds a cheaper nonland card and casting it for free resolves it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        // Cascade walks past a land, then hits Centaur Courser (MV 3 < the Sorcerer's MV 6).
        driver.putCardOnTopOfLibrary(me, "Centaur Courser")
        driver.putCardOnTopOfLibrary(me, "Forest")

        val sorcerer = driver.putCardInHand(me, "Etherium-Horn Sorcerer")
        driver.giveMana(me, Color.BLUE, 1)
        driver.giveMana(me, Color.RED, 1)
        driver.giveColorlessMana(me, 4)
        driver.submit(CastSpell(playerId = me, cardId = sorcerer, paymentStrategy = PaymentStrategy.FromPool))
            .isSuccess shouldBe true

        driver.bothPass() // the cascade trigger resolves and pauses on the free-cast offer
        withClue("cascade should pause with a may-cast decision") {
            driver.isPaused shouldBe true
            driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        }

        driver.submitYesNo(me, true).isSuccess shouldBe true
        driver.bothPass() // the free Centaur Courser resolves
        driver.bothPass() // then the Sorcerer itself resolves

        withClue("the cascaded creature hit the battlefield without being paid for") {
            driver.findPermanent(me, "Centaur Courser").shouldNotBeNull()
        }
        withClue("the Sorcerer resolves too") {
            driver.findPermanent(me, "Etherium-Horn Sorcerer").shouldNotBeNull()
        }
    }

    test("{1}{U}{R} returns the Sorcerer to its owner's hand") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val sorcerer = driver.putCreatureOnBattlefield(me, "Etherium-Horn Sorcerer")
        driver.giveMana(me, Color.BLUE, 1)
        driver.giveMana(me, Color.RED, 1)
        driver.giveColorlessMana(me, 1)

        val bounce = driver.cardRegistry.getCard("Etherium-Horn Sorcerer")!!.activatedAbilities[0].id
        driver.submit(ActivateAbility(playerId = me, sourceId = sorcerer, abilityId = bounce))
            .isSuccess shouldBe true
        driver.bothPass() // the ability resolves

        withClue("the Sorcerer left the battlefield") {
            driver.findPermanent(me, "Etherium-Horn Sorcerer") shouldBe null
        }
        withClue("...and is back in its owner's hand, ready to cascade again") {
            driver.findCardInHand(me, "Etherium-Horn Sorcerer").shouldNotBeNull()
        }
    }
})
