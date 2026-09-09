package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsDrawnEvent
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.handlers.DynamicAmountEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Mechanic coverage for [DynamicAmounts.greatestOwnedCommanderManaValue] and
 * [Patterns.Hand.eachPlayerMayDiscardHandAndDraw] — Imposing Grandeur's combined shape.
 *
 * Inline probe only; the VOC printing ships in a follow-up add-card cycle.
 */
class ImposingGrandeurMechanicTest : FunSpec({

    val fiveMvCommander = CardDefinition.creature(
        name = "Grandeur Five Commander",
        manaCost = ManaCost.parse("{3}{R}{R}"),
        subtypes = setOf(Subtype("Dragon")),
        power = 5,
        toughness = 5,
        supertypes = setOf(Supertype.LEGENDARY),
    )

    val twoMvCommander = CardDefinition.creature(
        name = "Grandeur Two Commander",
        manaCost = ManaCost.parse("{1}{R}"),
        subtypes = setOf(Subtype("Goblin")),
        power = 1,
        toughness = 1,
        supertypes = setOf(Supertype.LEGENDARY),
    )

    val imposingGrandeurProbe = card("Imposing Grandeur Probe") {
        manaCost = "{4}{R}"
        typeLine = "Sorcery"
        oracleText = "Each player may discard their hand and draw cards equal to the greatest " +
            "mana value of a commander they own on the battlefield or in the command zone."
        spell {
            effect = Patterns.Hand.eachPlayerMayDiscardHandAndDraw(
                DynamicAmounts.greatestOwnedCommanderManaValue(),
            )
        }
    }

    fun driver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + fiveMvCommander + twoMvCommander + imposingGrandeurProbe,
        )
        return driver
    }

    fun tagCommander(driver: GameTestDriver, entityId: EntityId, ownerId: EntityId) {
        driver.replaceState(
            driver.state.updateEntity(entityId) {
                val card = it.get<CardComponent>()
                val updated = if (card != null) it.with(card.copy(ownerId = ownerId)) else it
                updated.with(CommanderComponent(ownerId = ownerId))
            },
        )
    }

    fun fillHand(driver: GameTestDriver, playerId: EntityId, count: Int) {
        repeat(count) {
            driver.putCardInHand(playerId, "Island")
        }
    }

    fun clearHand(driver: GameTestDriver, playerId: EntityId) {
        while (driver.getHandSize(playerId) > 0) {
            driver.moveToGraveyard(driver.getHand(playerId).first())
        }
    }

    fun resolveSpellAcceptingAllMay(driver: GameTestDriver, accept: Boolean) {
        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 40) {
            val decision = driver.pendingDecision
            if (decision is YesNoDecision) {
                driver.submitYesNo(decision.playerId, accept)
            } else {
                driver.bothPass()
            }
        }
    }

    fun greatestOwnedMv(driver: GameTestDriver, playerId: EntityId): Int =
        DynamicAmountEvaluator().evaluate(
            driver.state,
            DynamicAmounts.greatestOwnedCommanderManaValue(),
            EffectContext(sourceId = null, controllerId = playerId),
        )

    test("greatestOwnedCommanderManaValue reads a commander in the command zone") {
        val driver = driver()
        val players = driver.initMultiplayer(
            decks = List(2) { Deck.of("Forest" to 40) },
            format = Format.Commander(),
            commanders = listOf(fiveMvCommander.name, twoMvCommander.name),
            skipMulligans = true,
        )
        greatestOwnedMv(driver, players[0]) shouldBe 5
        greatestOwnedMv(driver, players[1]) shouldBe 2
    }

    test("greatestOwnedCommanderManaValue takes the maximum across battlefield and command zone") {
        val driver = driver()
        val players = driver.initMultiplayer(
            decks = List(2) { Deck.of("Forest" to 40) },
            format = Format.Commander(),
            commanders = listOf(fiveMvCommander.name, twoMvCommander.name),
            skipMulligans = true,
        )
        val active = players[0]
        val commanderInZone = driver.state.getZone(ZoneKey(active, Zone.COMMAND)).single()
        driver.replaceState(
            driver.state
                .removeFromZone(ZoneKey(active, Zone.COMMAND), commanderInZone)
                .addToZone(ZoneKey(active, Zone.BATTLEFIELD), commanderInZone),
        )

        val onBattlefield = driver.putCreatureOnBattlefield(active, twoMvCommander.name)
        tagCommander(driver, onBattlefield, active)

        greatestOwnedMv(driver, active) shouldBe 5
    }

    test("a commander you own on the battlefield under another player's control still counts for you") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 40)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        val stolen = driver.putCreatureOnBattlefield(active, fiveMvCommander.name)
        tagCommander(driver, stolen, opponent)

        greatestOwnedMv(driver, opponent) shouldBe 5
        greatestOwnedMv(driver, active) shouldBe 0
    }

    test("battlefield-only mode ignores the command zone") {
        val driver = driver()
        val players = driver.initMultiplayer(
            decks = List(2) { Deck.of("Forest" to 40) },
            format = Format.Commander(),
            commanders = listOf(fiveMvCommander.name, twoMvCommander.name),
            skipMulligans = true,
        )
        val active = players[0]

        DynamicAmountEvaluator().evaluate(
            driver.state,
            DynamicAmounts.greatestOwnedCommanderManaValue(includeCommandZone = false),
            EffectContext(sourceId = null, controllerId = active),
        ) shouldBe 0
    }

    test("accepting the may discards and draws equal to the player's greatest owned commander mana value") {
        val driver = driver()
        val players = driver.initMultiplayer(
            decks = List(2) { Deck.of("Island" to 40) },
            format = Format.Commander(),
            commanders = listOf(fiveMvCommander.name, twoMvCommander.name),
            skipMulligans = true,
        )
        val active = players[0]
        val opponent = players[1]

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        clearHand(driver, active)
        clearHand(driver, opponent)
        fillHand(driver, active, 3)
        fillHand(driver, opponent, 4)

        val spell = driver.putCardInHand(active, "Imposing Grandeur Probe")
        driver.giveMana(active, Color.RED, 5)
        driver.castSpell(active, spell).isSuccess shouldBe true
        resolveSpellAcceptingAllMay(driver, accept = true)

        driver.getHandSize(active) shouldBe 5
        driver.getHandSize(opponent) shouldBe 2

        val draws = driver.events.filterIsInstance<CardsDrawnEvent>()
        draws.any { it.playerId == active && it.count == 5 } shouldBe true
        draws.any { it.playerId == opponent && it.count == 2 } shouldBe true
    }

    test("declining the may keeps the hand and draws nothing") {
        val driver = driver()
        val players = driver.initMultiplayer(
            decks = List(2) { Deck.of("Island" to 40) },
            format = Format.Commander(),
            commanders = listOf(fiveMvCommander.name, twoMvCommander.name),
            skipMulligans = true,
        )
        val active = players[0]
        val opponent = players[1]

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        clearHand(driver, active)
        clearHand(driver, opponent)
        fillHand(driver, active, 3)
        fillHand(driver, opponent, 4)

        val spell = driver.putCardInHand(active, "Imposing Grandeur Probe")
        driver.giveMana(active, Color.RED, 5)
        driver.castSpell(active, spell).isSuccess shouldBe true
        resolveSpellAcceptingAllMay(driver, accept = false)

        driver.getHandSize(active) shouldBe 3
        driver.getHandSize(opponent) shouldBe 4
    }

    test("accepting with no commanders discards the hand and draws zero") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        clearHand(driver, active)
        clearHand(driver, opponent)
        fillHand(driver, active, 2)

        val spell = driver.putCardInHand(active, "Imposing Grandeur Probe")
        driver.giveMana(active, Color.RED, 5)
        driver.castSpell(active, spell).isSuccess shouldBe true
        resolveSpellAcceptingAllMay(driver, accept = true)

        driver.getHandSize(active) shouldBe 0
        driver.getHandSize(opponent) shouldBe 0
    }

    test("IsCommander filter matches tagged commanders only") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val evaluator = PredicateEvaluator()

        val commander = driver.putCreatureOnBattlefield(active, fiveMvCommander.name)
        tagCommander(driver, commander, active)
        val notCommander = driver.putCreatureOnBattlefield(active, twoMvCommander.name)

        evaluator.matches(
            driver.state,
            driver.state.projectedState,
            commander,
            GameObjectFilter.Commander,
            PredicateContext(controllerId = active),
        ) shouldBe true

        evaluator.matches(
            driver.state,
            driver.state.projectedState,
            notCommander,
            GameObjectFilter.Commander,
            PredicateContext(controllerId = active),
        ) shouldBe false
    }
})
