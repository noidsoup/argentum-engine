package com.wingedsheep.engine.triggers

import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.event.TriggerDetector
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.state.components.stack.EntitySnapshot
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Feature F-EXILE-CAUSE: spell/ability exile cause attribution for batch exile triggers.
 *
 * Oracle shape under test: "Whenever one or more cards are put into exile from your hand or a spell
 * or ability you control exiles one or more permanents from the battlefield, …" (Hero of Bretagard,
 * Ranar the Ever-Watchful).
 */
class ExileCauseAttributionTest : FunSpec({

    val handOrByYouObserver = card("Hand Or By You Exile Observer") {
        manaCost = "{0}"
        typeLine = "Creature — Human Warrior"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.CardsPutIntoExileFromHandOrByYou()
            effect = Effects.DrawCards(1)
        }
    }

    val controlledBattlefieldOnly = card("Controlled Battlefield Exile Observer") {
        manaCost = "{0}"
        typeLine = "Creature — Human Cleric"
        power = 0
        toughness = 1
        triggeredAbility {
            trigger = Triggers.CardsPutIntoExile(
                fromZones = setOf(Zone.BATTLEFIELD),
                exilingControllerPredicate = ControllerPredicate.ControlledByYou,
                includeTokens = true,
            )
            effect = Effects.DrawCards(1)
        }
    }

    val bear = card("Exile Cause Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    val handCard = card("Exile Cause Hand Card") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "Draw a card."
        spell { effect = Effects.DrawCards(1) }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(handOrByYouObserver, controlledBattlefieldOnly, bear, handCard)
        )
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        return driver
    }

    fun exiledFromBattlefield(
        entityId: EntityId,
        controllerId: EntityId,
        ownerId: EntityId,
        exilingControllerId: EntityId? = null,
    ) = ZoneChangeEvent(
        entityId = entityId,
        entityName = "",
        fromZone = Zone.BATTLEFIELD,
        toZone = Zone.EXILE,
        ownerId = ownerId,
        lastKnown = EntitySnapshot(entityId = entityId, controllerId = controllerId),
        exilingControllerId = exilingControllerId,
    )

    fun exiledFromHand(entityId: EntityId, ownerId: EntityId) = ZoneChangeEvent(
        entityId = entityId,
        entityName = "",
        fromZone = Zone.HAND,
        toZone = Zone.EXILE,
        ownerId = ownerId,
    )

    fun exileTriggersOf(
        driver: GameTestDriver,
        events: List<ZoneChangeEvent>,
        sourceId: EntityId,
        trigger: (EventPattern) -> Boolean = { it is EventPattern.CardsPutIntoExileEvent },
    ) = TriggerDetector(driver.cardRegistry)
        .detectTriggers(driver.state, events)
        .filter { trigger(it.ability.trigger) && it.sourceId == sourceId }

    context("ZoneChangeEvent stamping") {

        test("markExileCause stamps exilingControllerId on the emitted ZoneChangeEvent") {
            val driver = createDriver()
            val bearId = driver.putCreatureOnBattlefield(driver.player1, "Exile Cause Bear")

            val marked = ZoneTransitionService.markExileCause(
                driver.state,
                listOf(bearId),
                driver.player1,
            )
            val result = ZoneTransitionService.moveToZone(marked, bearId, Zone.EXILE)
            val zoneEvent = result.events.filterIsInstance<ZoneChangeEvent>().single()

            zoneEvent.exilingControllerId shouldBe driver.player1
            result.state.pendingExileCauseControllers[bearId].shouldBeNull()
        }

        test("exile without markExileCause leaves exilingControllerId null") {
            val driver = createDriver()
            val bearId = driver.putCreatureOnBattlefield(driver.player1, "Exile Cause Bear")

            val result = ZoneTransitionService.moveToZone(driver.state, bearId, Zone.EXILE)
            val zoneEvent = result.events.filterIsInstance<ZoneChangeEvent>().single()

            zoneEvent.exilingControllerId.shouldBeNull()
        }

        test("ZoneEntryOptions.exileCauseControllerId stamps the event without markExileCause") {
            val driver = createDriver()
            val bearId = driver.putCreatureOnBattlefield(driver.player1, "Exile Cause Bear")

            val result = ZoneTransitionService.moveToZone(
                driver.state,
                bearId,
                Zone.EXILE,
                com.wingedsheep.engine.handlers.effects.ZoneEntryOptions(
                    exileCauseControllerId = driver.player2,
                ),
            )
            result.events.filterIsInstance<ZoneChangeEvent>().single().exilingControllerId shouldBe driver.player2
        }
    }

    context("hand-or-controlled battlefield batch trigger") {

        test("exiling a card from your hand fires the observer") {
            val driver = createDriver()
            val observer = driver.putCreatureOnBattlefield(driver.player1, "Hand Or By You Exile Observer")
            val cardId = driver.putCardInHand(driver.player1, "Exile Cause Hand Card")

            val triggers = exileTriggersOf(
                driver,
                listOf(exiledFromHand(cardId, driver.player1)),
                observer,
            )
            triggers shouldHaveSize 1
            triggers.single().triggerContext.capturedEntityIds shouldContainExactlyInAnyOrder listOf(cardId)
        }

        test("exiling a card from an opponent's hand does not fire") {
            val driver = createDriver()
            val observer = driver.putCreatureOnBattlefield(driver.player1, "Hand Or By You Exile Observer")
            val cardId = driver.putCardInHand(driver.player2, "Exile Cause Hand Card")

            exileTriggersOf(
                driver,
                listOf(exiledFromHand(cardId, driver.player2)),
                observer,
            ) shouldHaveSize 0
        }

        test("your spell exiling an opponent's permanent fires") {
            val driver = createDriver()
            val observer = driver.putCreatureOnBattlefield(driver.player1, "Hand Or By You Exile Observer")
            val theirs = driver.putCreatureOnBattlefield(driver.player2, "Exile Cause Bear")

            val triggers = exileTriggersOf(
                driver,
                listOf(
                    exiledFromBattlefield(
                        theirs,
                        driver.player2,
                        driver.player2,
                        exilingControllerId = driver.player1,
                    )
                ),
                observer,
            )
            triggers shouldHaveSize 1
        }

        test("opponent's spell exiling your permanent does not fire the hand-or-by-you observer") {
            val driver = createDriver()
            val observer = driver.putCreatureOnBattlefield(driver.player1, "Hand Or By You Exile Observer")
            val mine = driver.putCreatureOnBattlefield(driver.player1, "Exile Cause Bear")

            withClue("battlefield arm requires your spell/ability as the cause") {
                exileTriggersOf(
                    driver,
                    listOf(
                        exiledFromBattlefield(
                            mine,
                            driver.player1,
                            driver.player1,
                            exilingControllerId = driver.player2,
                        )
                    ),
                    observer,
                ) shouldHaveSize 0
            }
        }

        test("battlefield exile with no stamped cause does not fire the controlled arm") {
            val driver = createDriver()
            val observer = driver.putCreatureOnBattlefield(driver.player1, "Hand Or By You Exile Observer")
            val mine = driver.putCreatureOnBattlefield(driver.player1, "Exile Cause Bear")

            exileTriggersOf(
                driver,
                listOf(exiledFromBattlefield(mine, driver.player1, driver.player1)),
                observer,
            ) shouldHaveSize 0
        }

        test("mixed hand and controlled battlefield exiles batch once with both ids captured") {
            val driver = createDriver()
            val observer = driver.putCreatureOnBattlefield(driver.player1, "Hand Or By You Exile Observer")
            val handId = driver.putCardInHand(driver.player1, "Exile Cause Hand Card")
            val theirs = driver.putCreatureOnBattlefield(driver.player2, "Exile Cause Bear")

            val triggers = exileTriggersOf(
                driver,
                listOf(
                    exiledFromHand(handId, driver.player1),
                    exiledFromBattlefield(
                        theirs,
                        driver.player2,
                        driver.player2,
                        exilingControllerId = driver.player1,
                    ),
                ),
                observer,
            )
            triggers shouldHaveSize 1
            triggers.single().triggerContext.capturedEntityIds shouldContainExactlyInAnyOrder listOf(handId, theirs)
        }
    }

    context("controlled battlefield-only observer") {

        test("your spell exiling any battlefield permanent fires") {
            val driver = createDriver()
            val observer = driver.putCreatureOnBattlefield(driver.player1, "Controlled Battlefield Exile Observer")
            val theirs = driver.putCreatureOnBattlefield(driver.player2, "Exile Cause Bear")

            exileTriggersOf(
                driver,
                listOf(
                    exiledFromBattlefield(
                        theirs,
                        driver.player2,
                        driver.player2,
                        exilingControllerId = driver.player1,
                    )
                ),
                observer,
            ) shouldHaveSize 1
        }

        test("token permanent exiled by your spell counts when includeTokens is true") {
            val driver = createDriver()
            val observer = driver.putCreatureOnBattlefield(driver.player1, "Controlled Battlefield Exile Observer")
            val tokenId = driver.putCreatureOnBattlefield(driver.player1, "Exile Cause Bear")
            driver.replaceState(driver.state.updateEntity(tokenId) { it.with(TokenComponent) })

            driver.state.getEntity(tokenId)?.has<TokenComponent>() shouldBe true

            exileTriggersOf(
                driver,
                listOf(
                    exiledFromBattlefield(
                        tokenId,
                        driver.player1,
                        driver.player1,
                        exilingControllerId = driver.player1,
                    )
                ),
                observer,
            ) shouldHaveSize 1
        }
    }
})
