package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.roe.cards.BoarUmbra
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Umbra armor (CR 702.118) — exercised via Boar Umbra (ROE #179).
 * When the enchanted creature would be destroyed, remove all damage from it and destroy the Aura.
 */
class UmbraArmorScenarioTest : FunSpec({

    test("umbra armor saves the host from destruction and destroys the Aura") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(BoarUmbra))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40, "Swamp" to 10), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val player = driver.activePlayer!!
        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears")

        val umbra = driver.putCardInHand(player, "Boar Umbra")
        driver.giveMana(player, Color.GREEN, 3)
        driver.castSpell(player, umbra, targets = listOf(bears))
        driver.bothPass()
        driver.findPermanent(player, "Boar Umbra") shouldNotBe null

        val doom = driver.putCardInHand(player, "Doom Blade")
        driver.giveMana(player, Color.BLACK, 2)
        driver.castSpell(player, doom, targets = listOf(bears))
        driver.bothPass()

        driver.findPermanent(player, "Grizzly Bears") shouldNotBe null
        driver.findPermanent(player, "Boar Umbra") shouldBe null
        driver.assertInGraveyard(player, "Boar Umbra")
    }
})
