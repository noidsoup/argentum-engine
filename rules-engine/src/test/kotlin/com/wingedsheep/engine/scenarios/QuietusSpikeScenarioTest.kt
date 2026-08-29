package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Quietus Spike (ALA #217 / PC2 #112) — deathtouch equipment that halves the damaged player's life.
 */
class QuietusSpikeScenarioTest : FunSpec({

    fun GameTestDriver.putEquipmentAttached(
        playerId: EntityId,
        cardName: String,
        targetCreatureId: EntityId,
    ): EntityId {
        val equipmentId = putPermanentOnBattlefield(playerId, cardName)
        var newState = state.updateEntity(equipmentId) { c ->
            c.with(AttachedToComponent(targetCreatureId))
        }
        val existing = newState.getEntity(targetCreatureId)
            ?.get<AttachmentsComponent>()?.attachedIds ?: emptyList()
        newState = newState.updateEntity(targetCreatureId) { c ->
            c.with(AttachmentsComponent(existing + equipmentId))
        }
        replaceState(newState)
        return equipmentId
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("combat damage from the equipped creature halves the defending player's life rounded up") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 19)

        val attacker = driver.player1
        val defender = driver.player2

        val equipped = driver.putCreatureOnBattlefield(attacker, "Grizzly Bears")
        driver.removeSummoningSickness(equipped)
        driver.putEquipmentAttached(attacker, "Quietus Spike", equipped)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(attacker, listOf(equipped), defender)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)

        var safety = 0
        while (driver.getLifeTotal(defender) == 19 && safety++ < 20) {
            driver.bothPass()
        }

        withClue("19 life halved rounded up is 10") {
            driver.getLifeTotal(defender) shouldBe 10
        }
    }
})
