package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.CreatePredefinedTokenEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Predefined-token equipment grants — the SDK/engine primitives behind Arterial Alchemy's
 * "Blood tokens you control are Equipment … Equipped creature gets +2/+0 … equip {2}" line and
 * its ETB "create a Blood token for each opponent" line.
 */
class PredefinedTokenEquipmentGrantMechanicTest : FunSpec({

    val projector = StateProjector()

    val bloodEquipLord = card("Test Blood Equipment Lord") {
        manaCost = "{2}{B}"
        typeLine = "Enchantment"
        oracleText = "Blood tokens you control are Equipment in addition to their other types and " +
            "have \"Equipped creature gets +2/+0\" and equip {2}."
        for (static in Patterns.Token.grantBloodTokensAsEquipment()) {
            staticAbility { ability = static }
        }
    }

    val bloodWave = card("Test Blood Wave") {
        manaCost = "{B}"
        typeLine = "Sorcery"
        spell {
            effect = Patterns.Token.createBloodForEachOpponent()
        }
    }

    fun GameTestDriver.putEquipmentAttached(
        playerId: EntityId,
        equipmentName: String,
        creatureId: EntityId,
    ): EntityId {
        val equipmentId = putPermanentOnBattlefield(playerId, equipmentName)
        var newState = state.updateEntity(equipmentId) { c ->
            c.with(AttachedToComponent(creatureId))
        }
        val existing = newState.getEntity(creatureId)
            ?.get<AttachmentsComponent>()?.attachedIds ?: emptyList()
        newState = newState.updateEntity(creatureId) { c ->
            c.with(AttachmentsComponent(existing + equipmentId))
        }
        replaceState(newState)
        return equipmentId
    }

    fun battlefieldBloodTokens(driver: GameTestDriver, controllerId: EntityId): List<EntityId> =
        driver.state.getBattlefield().filter { entityId ->
            val card = driver.state.getEntity(entityId)?.get<CardComponent>() ?: return@filter false
            card.typeLine.hasSubtype(Subtype("Blood")) &&
                driver.state.projectedState.getController(entityId) == controllerId
        }

    test("creatures equipped to blood tokens get the granted power bonus") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + PredefinedTokens.allTokens + listOf(bloodEquipLord))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.player1
        driver.putPermanentOnBattlefield(you, "Test Blood Equipment Lord")
        val bear = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        driver.putEquipmentAttached(you, "Blood", bear)

        val projected = projector.project(driver.state)
        projected.getPower(bear) shouldBe 4 // 2 base + 2 from the lord's equipment pump
    }

    test("blood tokens gain Equipment type while the lord is on the battlefield") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + PredefinedTokens.allTokens + listOf(bloodEquipLord))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.player1
        driver.putPermanentOnBattlefield(you, "Test Blood Equipment Lord")
        val blood = driver.putPermanentOnBattlefield(you, "Blood")

        val projected = projector.project(driver.state)
        projected.hasSubtype(blood, "Equipment") shouldBe true
    }

    test("blood tokens are not Equipment without the lord") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + PredefinedTokens.allTokens)
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.player1
        val blood = driver.putPermanentOnBattlefield(you, "Blood")

        val projected = projector.project(driver.state)
        projected.hasSubtype(blood, "Equipment") shouldBe false
    }

    test("createBloodForEachOpponent creates one blood token per opponent in a two-player game") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + PredefinedTokens.allTokens + listOf(bloodWave))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val you = driver.player1
        val spell = driver.putCardInHand(you, "Test Blood Wave")
        driver.giveMana(you, Color.BLACK, 5)
        driver.castSpell(you, spell).error shouldBe null

        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 20) {
            driver.bothPass().error shouldBe null
        }

        battlefieldBloodTokens(driver, you) shouldHaveSize 1
    }

    test("createBloodForEachOpponent is a dynamic Blood token creation effect") {
        val effect = Patterns.Token.createBloodForEachOpponent()
        effect.shouldBeInstanceOf<CreatePredefinedTokenEffect>()
        effect.tokenType shouldBe "Blood"
        effect.dynamicCount shouldBe DynamicAmount.PlayerCount(Player.EachOpponent)
    }
})
