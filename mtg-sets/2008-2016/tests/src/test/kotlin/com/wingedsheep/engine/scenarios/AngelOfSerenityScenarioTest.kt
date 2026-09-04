package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rtr.cards.AngelOfSerenity
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Angel of Serenity (RTR) — {4}{W}{W}{W} 5/6 Angel with flying.
 *
 * "When this creature enters, you may exile up to three other target creatures from the battlefield
 *  and/or creature cards from graveyards."
 * "When this creature leaves the battlefield, return the exiled cards to their owners' hands."
 */
class AngelOfSerenityScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(AngelOfSerenity))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castAngel(player: EntityId): EntityId {
        val angel = putCardInHand(player, "Angel of Serenity")
        giveMana(player, Color.WHITE, 7)
        submit(
            CastSpell(
                playerId = player,
                cardId = angel,
                paymentStrategy = PaymentStrategy.FromPool,
            )
        ).error shouldBe null
        return angel
    }

    fun GameTestDriver.resolveUntilTargetChoice(): ChooseTargetsDecision {
        var guard = 0
        while (guard++ < 40) {
            val decision = pendingDecision
            if (decision is ChooseTargetsDecision) return decision
            if (stackSize > 0 || decision != null) bothPass() else break
        }
        error("expected ChooseTargetsDecision for the ETB exile; got $pendingDecision")
    }

    test("ETB exiles up to three targets — battlefield creatures and graveyard creature cards") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val courser = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")
        val lions = driver.putCardInGraveyard(opponent, "Savannah Lions")
        val bear = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        driver.castAngel(player)
        val decision = driver.resolveUntilTargetChoice()

        withClue("the union offers battlefield creatures and graveyard creature cards") {
            val offered = decision.legalTargets[0].orEmpty()
            offered shouldContain courser
            offered shouldContain lions
            offered shouldContain bear
        }

        withClue("up to three targets are allowed") {
            decision.targetRequirements[0].maxTargets shouldBe 3
        }

        driver.submitTargetSelection(player, listOf(courser, lions, bear)).error shouldBe null
        repeat(3) { if (!driver.isPaused && driver.stackSize > 0) driver.bothPass() }

        withClue("all three chosen objects are exiled") {
            driver.getExile(opponent) shouldContainExactlyInAnyOrder listOf(courser, lions, bear)
        }
        withClue("the Angel is a 5/6 flier on the battlefield") {
            val angelPerm = driver.getPermanents(player).single {
                driver.getCardName(it) == "Angel of Serenity"
            }
            driver.state.projectedState.getPower(angelPerm) shouldBe 5
            driver.state.projectedState.getToughness(angelPerm) shouldBe 6
            driver.state.projectedState.hasKeyword(angelPerm, Keyword.FLYING) shouldBe true
        }
    }

    test("declining all targets exiles nothing") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        driver.castAngel(player)
        driver.resolveUntilTargetChoice()

        driver.submitTargetSelection(player, emptyList()).error shouldBe null
        repeat(3) { if (!driver.isPaused && driver.stackSize > 0) driver.bothPass() }

        withClue("no cards were exiled") {
            driver.getExile(opponent) shouldBe emptyList()
        }
    }

    test("when it leaves the battlefield the exiled cards return to their owners' hands") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val courser = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")
        val lions = driver.putCardInGraveyard(opponent, "Savannah Lions")

        driver.castAngel(player)
        driver.resolveUntilTargetChoice()
        driver.submitTargetSelection(player, listOf(courser, lions)).error shouldBe null
        repeat(3) { if (!driver.isPaused && driver.stackSize > 0) driver.bothPass() }

        val angelPerm = driver.getPermanents(player).single {
            driver.getCardName(it) == "Angel of Serenity"
        }
        driver.getExile(opponent) shouldContainExactlyInAnyOrder listOf(courser, lions)

        val murder = driver.putCardInHand(player, "Murder")
        driver.giveMana(player, Color.BLACK, 3)
        driver.castSpellWithTargets(
            player,
            murder,
            listOf(com.wingedsheep.engine.state.components.stack.ChosenTarget.Permanent(angelPerm)),
        ).error shouldBe null
        repeat(3) { if (!driver.isPaused && driver.stackSize > 0) driver.bothPass() }

        withClue("both exiled cards return to hand, not the battlefield") {
            driver.getHand(opponent) shouldContain courser
            driver.getHand(opponent) shouldContain lions
        }
        withClue("nothing remains in exile") {
            driver.getExile(opponent).none { it == courser || it == lions } shouldBe true
        }
        withClue("the Angel is in its owner's graveyard") {
            driver.findCardInHand(player, "Angel of Serenity").shouldBeNull()
            driver.getGraveyard(player).any { driver.getCardName(it) == "Angel of Serenity" } shouldBe true
        }
    }
})
