package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * Return to Dust — {2}{W}{W} Instant
 *
 * Exile target artifact or enchantment. If you cast this spell during your main phase, you may
 * exile up to one other target artifact or enchantment.
 */
class ReturnToDustScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    fun giveManaForReturnToDust(driver: GameTestDriver, caster: com.wingedsheep.sdk.model.EntityId) {
        driver.giveMana(caster, Color.WHITE, 4)
    }

    test("cast during your main phase may exile a second artifact or enchantment") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        val dust = driver.putCardInHand(caster, "Return to Dust")
        val artifact = driver.putPermanentOnBattlefield(opponent, "Icy Manipulator")
        val enchantment = driver.putPermanentOnBattlefield(opponent, "Test Enchantment")

        giveManaForReturnToDust(driver, caster)

        driver.castSpellWithTargets(
            caster,
            dust,
            listOf(ChosenTarget.Permanent(artifact), ChosenTarget.Permanent(enchantment)),
        ).error shouldBe null
        driver.bothPass()

        driver.getExileCardNames(opponent) shouldContainExactlyInAnyOrder listOf(
            "Icy Manipulator",
            "Test Enchantment",
        )
    }

    test("cast during your main phase with only one target exiles just that permanent") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        val dust = driver.putCardInHand(caster, "Return to Dust")
        val artifact = driver.putPermanentOnBattlefield(opponent, "Icy Manipulator")
        driver.putPermanentOnBattlefield(opponent, "Test Enchantment")

        giveManaForReturnToDust(driver, caster)

        driver.castSpellWithTargets(caster, dust, listOf(ChosenTarget.Permanent(artifact)))
            .error shouldBe null
        driver.bothPass()

        driver.getExileCardNames(opponent) shouldContainExactlyInAnyOrder listOf("Icy Manipulator")
    }

    test("cast outside your main phase exiles only the required target") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val caster = driver.activePlayer!!
        val opponent = driver.getOpponent(caster)
        val dust = driver.putCardInHand(caster, "Return to Dust")
        val artifact = driver.putPermanentOnBattlefield(opponent, "Icy Manipulator")

        // Leave precombat main for combat — no longer "your main phase".
        driver.bothPass()
        driver.bothPass()
        withClue("should be in combat after both players pass in main") {
            driver.state.phase shouldBe Phase.COMBAT
        }

        giveManaForReturnToDust(driver, caster)

        driver.castSpellWithTargets(caster, dust, listOf(ChosenTarget.Permanent(artifact)))
            .error shouldBe null
        driver.bothPass()

        driver.getExileCardNames(opponent) shouldContainExactlyInAnyOrder listOf("Icy Manipulator")
    }
})
