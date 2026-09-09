package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.mechanics.layers.ActiveFloatingEffect
import com.wingedsheep.engine.mechanics.layers.FloatingEffectData
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.echo.EchoUpkeepTracking
import com.wingedsheep.engine.mechanics.layers.addFloatingEffects
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Mechanic-level tests for Echo [cost] (CR 702.30).
 *
 * A card carrying `Echo {2}` declares one keyword ability; the engine supplies the upkeep
 * sacrifice-unless-pay trigger from [com.wingedsheep.sdk.scripting.Echo].
 */
class EchoKeywordTest : FunSpec({

    val echoBear = card("Echo Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
        oracleText = "Echo {2}"
        keywordAbility(KeywordAbility.echo("{2}"))
    }

    val plainBear = card("Plain Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(echoBear, plainBear))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun castAndResolve(driver: GameTestDriver, player: EntityId, cardName: String) {
        driver.giveMana(player, Color.GREEN, 3)
        val cardId = driver.putCardInHand(player, cardName)
        val result = driver.submit(
            CastSpell(player, cardId, emptyList())
        )
        if (!result.isSuccess) throw AssertionError("cast of $cardName failed: ${result.error}")
        driver.bothPass()
    }

    /** Advance to [owner]'s next upkeep and resolve the echo trigger sitting on the stack. */
    fun resolveNextOwnerUpkeep(driver: GameTestDriver, owner: EntityId) {
        do {
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
            driver.passPriorityUntil(Step.UPKEEP)
        } while (driver.activePlayer != owner)
        driver.bothPass()
    }

    test("echo does not fire on the turn the creature is cast") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        castAndResolve(driver, player, "Echo Bear")
        driver.findPermanent(player, "Echo Bear") shouldBe driver.findPermanent(player, "Echo Bear")
        resolveNextOwnerUpkeep(driver, player)
        // No mana to pay {2} — the trigger auto-sacrifices without a decision prompt.
        driver.findPermanent(player, "Echo Bear").shouldBeNull()
    }

    test("a permanent present at the controller's last upkeep does not echo again") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        castAndResolve(driver, player, "Echo Bear")
        val bear = driver.findPermanent(player, "Echo Bear")!!

        // Simulate surviving last upkeep's echo payment: the end-of-upkeep stamp records that
        // this permanent was already under the controller when their previous upkeep closed.
        driver.replaceState(EchoUpkeepTracking.stampPresentAtUpkeep(driver.state, player))

        resolveNextOwnerUpkeep(driver, player)
        driver.findPermanent(player, "Echo Bear") shouldBe bear
    }

    test("a creature that merely gains echo echoes on the controller's next upkeep") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        castAndResolve(driver, player, "Plain Bear")
        val bear = driver.findPermanent(player, "Plain Bear")!!

        val s = driver.state.addFloatingEffects(
            listOf(
                ActiveFloatingEffect(
                    id = EntityId.generate(),
                    effect = FloatingEffectData(
                        layer = Layer.ABILITY,
                        modification = SerializableModification.GrantKeyword(Keyword.ECHO.name),
                        affectedEntities = setOf(bear),
                    ),
                    duration = Duration.Permanent,
                    sourceId = bear,
                    sourceName = "Plain Bear",
                    controllerId = player,
                    timestamp = driver.state.timestamp,
                )
            )
        )
        driver.replaceState(s)
        driver.state.projectedState.hasKeyword(bear, Keyword.ECHO) shouldBe true

        resolveNextOwnerUpkeep(driver, player)
        driver.findPermanent(player, "Plain Bear").shouldBeNull()
    }

    test("Echo.printedCosts reads each printed instance") {
        val doubled = card("Doubly Echo Bear") {
            manaCost = "{1}{G}"
            typeLine = "Creature — Bear"
            power = 2
            toughness = 2
            keywordAbilities(
                KeywordAbility.echo("{1}"),
                KeywordAbility.echo("{2}"),
            )
        }
        com.wingedsheep.sdk.scripting.Echo.printedCosts(doubled) shouldBe
            listOf(ManaCost.parse("{1}"), ManaCost.parse("{2}"))
        com.wingedsheep.sdk.scripting.Echo.printedCosts(plainBear) shouldBe emptyList()
    }
})
