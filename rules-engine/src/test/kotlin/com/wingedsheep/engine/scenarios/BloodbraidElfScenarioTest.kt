package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.arb.cards.BloodbraidElf
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
 * Bloodbraid Elf (ARB #50) — {2}{R}{G} 3/2 Elf Berserker with haste and cascade.
 *
 * Cascade (CR 702.85a) exiles from the top of the library until a nonland card with a *lesser* mana
 * value shows up, then offers to cast it for free. Bloodbraid Elf's mana value is 4, so these tests
 * pin the strictly-less-than comparison (Force of Nature at MV 5 is walked past, Centaur Courser at
 * MV 3 is the hit) and both branches of the free-cast offer.
 */
class BloodbraidElfScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(BloodbraidElf)
        return driver
    }

    /** {2}{R}{G} straight from the pool. */
    fun GameTestDriver.fundTheElf(me: EntityId) {
        giveMana(me, Color.RED, 1)
        giveMana(me, Color.GREEN, 1)
        giveColorlessMana(me, 2)
    }

    test("the printed card carries haste and cascade") {
        val elf = BloodbraidElf
        withClue("Bloodbraid Elf is a hasty cascader") {
            elf.keywords.contains(Keyword.HASTE) shouldBe true
            elf.keywords.contains(Keyword.CASCADE) shouldBe true
        }
    }

    test("cascade finds a cheaper nonland card and casting it for free resolves it") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        // Cascade walks past a land, then hits Centaur Courser (MV 3 < the Elf's MV 4).
        driver.putCardOnTopOfLibrary(me, "Centaur Courser")
        driver.putCardOnTopOfLibrary(me, "Forest")

        val elf = driver.putCardInHand(me, "Bloodbraid Elf")
        driver.fundTheElf(me)
        driver.submit(CastSpell(playerId = me, cardId = elf, paymentStrategy = PaymentStrategy.FromPool))
            .isSuccess shouldBe true

        driver.bothPass() // the cascade trigger resolves and pauses on the free-cast offer
        withClue("cascade should pause with a may-cast decision") {
            driver.isPaused shouldBe true
            driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        }

        driver.submitYesNo(me, true).isSuccess shouldBe true
        driver.bothPass() // the free Centaur Courser resolves
        driver.bothPass() // then Bloodbraid Elf itself resolves

        withClue("the cascaded creature hit the battlefield without being paid for") {
            driver.findPermanent(me, "Centaur Courser").shouldNotBeNull()
        }
        withClue("Bloodbraid Elf resolves too — cascade doesn't consume it") {
            driver.findPermanent(me, "Bloodbraid Elf").shouldNotBeNull()
        }
    }

    test("declining the free cast leaves the cascaded card off the battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        driver.putCardOnTopOfLibrary(me, "Centaur Courser")
        driver.putCardOnTopOfLibrary(me, "Forest")

        val elf = driver.putCardInHand(me, "Bloodbraid Elf")
        driver.fundTheElf(me)
        driver.submit(CastSpell(playerId = me, cardId = elf, paymentStrategy = PaymentStrategy.FromPool))
            .isSuccess shouldBe true

        driver.bothPass()
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        driver.submitYesNo(me, false).isSuccess shouldBe true
        driver.bothPass() // Bloodbraid Elf resolves

        withClue("cascade is a 'may' — declining casts nothing") {
            driver.findPermanent(me, "Centaur Courser") shouldBe null
        }
        withClue("the Elf still resolves") {
            driver.findPermanent(me, "Bloodbraid Elf").shouldNotBeNull()
        }
    }

    test("cascade walks past a nonland card whose mana value is not less than the Elf's") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        // Top of library downward: Force of Nature (MV 5 — not less than 4), then Centaur Courser (MV 3).
        driver.putCardOnTopOfLibrary(me, "Centaur Courser")
        driver.putCardOnTopOfLibrary(me, "Force of Nature")

        val elf = driver.putCardInHand(me, "Bloodbraid Elf")
        driver.fundTheElf(me)
        driver.submit(CastSpell(playerId = me, cardId = elf, paymentStrategy = PaymentStrategy.FromPool))
            .isSuccess shouldBe true

        driver.bothPass()
        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()

        driver.submitYesNo(me, true).isSuccess shouldBe true
        driver.bothPass()
        driver.bothPass()

        withClue("the MV 3 card is the hit, not the MV 5 one above it") {
            driver.findPermanent(me, "Centaur Courser").shouldNotBeNull()
            driver.findPermanent(me, "Force of Nature") shouldBe null
        }
    }
})
