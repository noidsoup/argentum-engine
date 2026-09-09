package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.ConditionEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.state.components.identity.LifeTotalComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Mechanic coverage for [Conditions.PlayerControlsCommander] and [Effects.DealDamageUnless] —
 * Crimson Honor Guard's "at the beginning of each player's end step, ~ deals 4 damage to that
 * player unless they control a commander" shape.
 *
 * Inline probe card only; the VOC printing ships in a follow-up add-card cycle.
 */
class PlayerControlsCommanderAndDealDamageUnlessMechanicTest : FunSpec({

    val testCommander = CardDefinition.creature(
        name = "Honor Guard Test Commander",
        manaCost = ManaCost.parse("{3}{R}{R}"),
        subtypes = setOf(Subtype("Vampire"), Subtype("Knight")),
        power = 4,
        toughness = 5,
        supertypes = setOf(Supertype.LEGENDARY),
    )

    val honorGuardProbe = card("Honor Guard Probe") {
        manaCost = "{3}{R}{R}"
        colorIdentity = "R"
        typeLine = "Creature — Vampire Knight"
        oracleText = "Trample\nAt the beginning of each player's end step, Honor Guard Probe deals " +
            "4 damage to that player unless they control a commander."
        power = 4
        toughness = 5
        triggeredAbility {
            trigger = Triggers.EachEndStep
            effect = Effects.DealDamageUnless(
                unless = Conditions.PlayerControlsCommander(Player.TriggeringPlayer),
                amount = 4,
                target = EffectTarget.PlayerRef(Player.TriggeringPlayer),
                damageSource = EffectTarget.Self,
            )
        }
    }

    fun driver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + testCommander + honorGuardProbe)
        return driver
    }

    fun tagCommander(driver: GameTestDriver, entityId: EntityId, ownerId: EntityId) {
        driver.replaceState(
            driver.state.updateEntity(entityId) {
                it.with(CommanderComponent(ownerId = ownerId))
            }
        )
    }

    fun life(driver: GameTestDriver, playerId: EntityId): Int =
        driver.state.getEntity(playerId)?.get<LifeTotalComponent>()?.life ?: 0

    test("PlayerControlsCommander is true for a commander in the command zone") {
        val driver = driver()
        val players = driver.initMultiplayer(
            decks = List(2) { Deck.of("Forest" to 40) },
            format = Format.Commander(),
            commanders = listOf(testCommander.name, testCommander.name),
            skipMulligans = true,
        )
        val active = players[0]
        ConditionEvaluator().evaluate(
            driver.state,
            Conditions.PlayerControlsCommander(Player.You),
            EffectContext(sourceId = null, controllerId = active),
        ) shouldBe true
    }

    test("PlayerControlsCommander is false when the commander left for the graveyard") {
        val driver = driver()
        val players = driver.initMultiplayer(
            decks = List(2) { Deck.of("Forest" to 40) },
            format = Format.Commander(),
            commanders = listOf(testCommander.name, testCommander.name),
            skipMulligans = true,
        )
        val active = players[0]
        val commanderId = driver.state.getZone(ZoneKey(active, Zone.COMMAND)).single()
        driver.replaceState(
            driver.state
                .removeFromZone(ZoneKey(active, Zone.COMMAND), commanderId)
                .addToZone(ZoneKey(active, Zone.GRAVEYARD), commanderId)
        )

        ConditionEvaluator().evaluate(
            driver.state,
            Conditions.PlayerControlsCommander(Player.You),
            EffectContext(sourceId = null, controllerId = active),
        ) shouldBe false
    }

    test("PlayerControlsCommander is true when controlling another player's commander on the battlefield") {
        val driver = driver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            startingLife = 40,
        )
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        val stolenCommander = driver.putCreatureOnBattlefield(active, testCommander.name)
        tagCommander(driver, stolenCommander, opponent)

        ConditionEvaluator().evaluate(
            driver.state,
            Conditions.PlayerControlsCommander(Player.You),
            EffectContext(sourceId = stolenCommander, controllerId = active),
        ) shouldBe true
    }

    fun runEndStep(driver: GameTestDriver) {
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
    }

    test("deals 4 at the active player's end step when they control no commander") {
        val driver = driver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            startingLife = 40,
        )
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.putCreatureOnBattlefield(active, "Honor Guard Probe")
        runEndStep(driver)

        life(driver, active) shouldBe 36
        life(driver, opponent) shouldBe 40
    }

    test("skips damage when the active player still has a commander in the command zone") {
        val driver = driver()
        val players = driver.initMultiplayer(
            decks = List(2) { Deck.of("Forest" to 40) },
            format = Format.Commander(),
            commanders = listOf(testCommander.name, testCommander.name),
            skipMulligans = true,
        )
        val active = players[0]
        val opponent = players[1]

        driver.putCreatureOnBattlefield(active, "Honor Guard Probe")
        runEndStep(driver)

        life(driver, active) shouldBe 40
        life(driver, opponent) shouldBe 40
    }

    test("the probe's controller is not spared when they have no commander") {
        val driver = driver()
        driver.initMultiplayer(
            decks = List(2) { Deck.of("Forest" to 40) },
            format = Format.Commander(),
            commanders = listOf(testCommander.name, testCommander.name),
            skipMulligans = true,
        )
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        val probe = driver.putCreatureOnBattlefield(active, "Honor Guard Probe")
        val commanderId = driver.state.getZone(ZoneKey(active, Zone.COMMAND)).single()
        driver.replaceState(
            driver.state
                .removeFromZone(ZoneKey(active, Zone.COMMAND), commanderId)
                .addToZone(ZoneKey(active, Zone.GRAVEYARD), commanderId)
        )

        runEndStep(driver)

        life(driver, active) shouldBe 36
        life(driver, opponent) shouldBe 40
    }

    test("a battlefield commander under the active player's control prevents the damage") {
        val driver = driver()
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 40),
            startingLife = 40,
        )
        val active = driver.activePlayer!!

        driver.putCreatureOnBattlefield(active, "Honor Guard Probe")
        val battlefieldCommander = driver.putCreatureOnBattlefield(active, testCommander.name)
        tagCommander(driver, battlefieldCommander, active)

        runEndStep(driver)

        life(driver, active) shouldBe 40
    }
})
