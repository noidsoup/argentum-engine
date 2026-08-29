package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.DimirInfiltrator
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Dimir Infiltrator (RAV #203) — {U}{B} 1/3 Creature — Spirit.
 *
 * "This creature can't be blocked.
 *  Transmute {1}{U}{B} ({1}{U}{B}, Discard this card: Search your library for a card with the same
 *  mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"
 */
class DimirInfiltratorScenarioTest : FunSpec({

    val transmuteId = DimirInfiltrator.activatedAbilities.first().id

    test("transmute from hand finds a card with the same mana value as this card") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(DimirInfiltrator)
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Grizzly Bears is MV 2, same as Dimir Infiltrator ({U}{B}).
        val bears = driver.putCardOnTopOfLibrary(player, "Grizzly Bears")
        val infiltrator = driver.putCardInHand(player, "Dimir Infiltrator")
        driver.giveMana(player, Color.BLUE, 2)
        driver.giveMana(player, Color.BLACK, 2)

        driver.submit(
            ActivateAbility(playerId = player, sourceId = infiltrator, abilityId = transmuteId)
        ).isSuccess shouldBe true
        driver.bothPass()

        if (driver.isPaused) {
            val decision = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
            decision.options.contains(bears) shouldBe true
            driver.submitCardSelection(player, listOf(bears))
        }
        driver.isPaused shouldBe false

        driver.getHand(player).contains(infiltrator) shouldBe false
        driver.getGraveyard(player).contains(infiltrator) shouldBe true
        driver.getHand(player).contains(bears) shouldBe true
    }
})
