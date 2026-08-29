package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.pc2.cards.SakashimasStudent
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Sakashima's Student (PC2 #24) — ninjutsu and optional enter-as-copy with the Ninja subtype rider.
 */
class SakashimasStudentScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SakashimasStudent))
        return driver
    }

    test("entering as a copy keeps the copied name and adds Ninja") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bears = driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val student = driver.putCardInHand(me, "Sakashima's Student")
        driver.giveMana(me, Color.BLUE, 4)
        driver.castSpell(me, student)
        driver.bothPass()

        val decision = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.options shouldContain bears
        driver.submitCardSelection(me, listOf(bears))

        projector.getProjectedPower(driver.state, student) shouldBe 2
        projector.getProjectedToughness(driver.state, student) shouldBe 2
        driver.state.getEntity(student)?.get<CardComponent>()!!.name shouldBe "Grizzly Bears"

        val subtypes = projector.project(driver.state).getSubtypes(student)
        withClue("copy keeps Bear and gains Ninja (subtypes=$subtypes)") {
            subtypes.any { it.equals("Bear", ignoreCase = true) }.shouldBeTrue()
            subtypes.any { it.equals("Ninja", ignoreCase = true) }.shouldBeTrue()
        }
    }

    test("declining the copy enters as the printed 0/0 Human Ninja") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Without a boost, a declined 0/0 dies to state-based actions immediately.
        driver.putPermanentOnBattlefield(me, "Glorious Anthem")
        driver.putCreatureOnBattlefield(me, "Grizzly Bears")
        val student = driver.putCardInHand(me, "Sakashima's Student")
        driver.giveMana(me, Color.BLUE, 4)
        driver.castSpell(me, student)
        driver.bothPass()

        driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitCardSelection(me, emptyList())
        var safety = 0
        while (safety++ < 20 && (driver.state.stack.isNotEmpty() || driver.pendingDecision != null)) {
            driver.bothPass()
        }

        val onBattlefield = driver.state.getBattlefield().first { id ->
            driver.state.getEntity(id)?.get<CardComponent>()?.name == "Sakashima's Student"
        }
        projector.getProjectedPower(driver.state, onBattlefield) shouldBe 1
        projector.getProjectedToughness(driver.state, onBattlefield) shouldBe 1
        driver.state.getEntity(onBattlefield)?.get<CardComponent>()!!.name shouldBe "Sakashima's Student"

        val subtypes = projector.project(driver.state).getSubtypes(onBattlefield)
        withClue("keeps Human and Ninja (subtypes=$subtypes)") {
            subtypes.any { it.equals("Human", ignoreCase = true) }.shouldBeTrue()
            subtypes.any { it.equals("Ninja", ignoreCase = true) }.shouldBeTrue()
        }
    }
})
