package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.chk.cards.GhostlyPrison
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Ghostly Prison (CHK #10, PC2 #7) — {2}{W} Enchantment.
 * Creatures can't attack you unless their controller pays {2} for each attacking creature.
 */
class GhostlyPrisonScenarioTest : FunSpec({

    val Bear = CardDefinition.creature(
        name = "Test Bear",
        manaCost = com.wingedsheep.sdk.core.ManaCost.parse("{1}{G}"),
        subtypes = setOf(com.wingedsheep.sdk.core.Subtype("Bear")),
        power = 2,
        toughness = 2,
        oracleText = "",
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(GhostlyPrison, Bear))
        return driver
    }

    test("Ghostly Prison taxes attacks against its controller") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30, "Forest" to 30), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        driver.putPermanentOnBattlefield(defender, "Ghostly Prison")
        val bear = driver.putCreatureOnBattlefield(attacker, "Test Bear")
        driver.removeSummoningSickness(bear)
        driver.putPermanentOnBattlefield(attacker, "Plains")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        val result = driver.declareAttackers(attacker, listOf(bear), defender)
        result.newState.pendingDecision.shouldBeInstanceOf<SelectManaSourcesDecision>()
    }

    test("attacks proceed when Ghostly Prison is not on the battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30, "Forest" to 30), startingLife = 20)

        val attacker = driver.activePlayer!!
        val defender = driver.getOpponent(attacker)

        val bear = driver.putCreatureOnBattlefield(attacker, "Test Bear")
        driver.removeSummoningSickness(bear)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        val result = driver.declareAttackers(attacker, listOf(bear), defender)

        result.isSuccess shouldBe true
        (result.newState.pendingDecision is SelectManaSourcesDecision) shouldBe false
    }
})
