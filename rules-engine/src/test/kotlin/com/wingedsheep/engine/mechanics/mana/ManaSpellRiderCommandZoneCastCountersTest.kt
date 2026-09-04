package com.wingedsheep.engine.mechanics.mana

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CommanderComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Format
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.ManaSpellRider
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

/**
 * Mechanic tests for [ManaSpellRider.EntersWithCountersPerCommandZoneCast] (Opal Palace).
 *
 * The rider freezes an entry-rider onto a commander spell at payment time and applies +1/+1
 * counters at resolution equal to [CommanderComponent.castsFromCommandZone] — after the
 * cast-commit increment for a command-zone cast.
 */
class ManaSpellRiderCommandZoneCastCountersTest : FunSpec({

    val riderLandAbilityId = AbilityId(UUID.randomUUID().toString())

    val testRiderLand = CardDefinition(
        name = "Test Opal Rider Land",
        manaCost = ManaCost.ZERO,
        typeLine = TypeLine.parse("Land"),
        oracleText = "{T}: Add {R}. If you spend this mana to cast your commander, it enters with " +
            "+1/+1 counters equal to times cast from the command zone.",
        script = CardScript.permanent(
            ActivatedAbility(
                id = riderLandAbilityId,
                cost = AbilityCost.Tap,
                effect = AddManaEffect(
                    color = Color.RED,
                    riders = setOf(ManaSpellRider.EntersWithCountersPerCommandZoneCast()),
                ),
                isManaAbility = true,
                timing = TimingRule.ManaAbility,
            )
        ),
    )

    val nonCommander = CardDefinition.creature(
        name = "Test Non Commander",
        manaCost = ManaCost.parse("{R}"),
        subtypes = setOf(Subtype("Goblin")),
        power = 1,
        toughness = 1,
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(testRiderLand, nonCommander))
        driver.initMultiplayer(
            decks = listOf(Deck.of("Mountain" to 99), Deck.of("Mountain" to 99)),
            format = Format.Commander(),
            commanders = listOf("Test Hasty Prospector", "Test Hasty Prospector"),
        )
        return driver
    }

    fun tapRiderLand(driver: GameTestDriver, playerId: EntityId, landId: EntityId) {
        driver.submitSuccess(
            ActivateAbility(playerId = playerId, sourceId = landId, abilityId = riderLandAbilityId)
        )
    }

    fun plusOneCount(driver: GameTestDriver, entityId: EntityId): Int =
        driver.state.getEntity(entityId)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    test("first command-zone cast with rider mana enters with one +1/+1 counter") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putPermanentOnBattlefield(you, "Test Opal Rider Land")
        val commanderId = driver.state.getZone(ZoneKey(you, Zone.COMMAND)).single()
        tapRiderLand(driver, you, land)

        driver.castSpell(you, commanderId).error shouldBe null
        driver.bothPass()

        plusOneCount(driver, commanderId) shouldBe 1
        driver.state.getEntity(commanderId)?.get<CommanderComponent>()?.castsFromCommandZone shouldBe 1
    }

    test("second command-zone cast with rider mana enters with two +1/+1 counters") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putPermanentOnBattlefield(you, "Test Opal Rider Land")
        var commanderId = driver.state.getZone(ZoneKey(you, Zone.COMMAND)).single()

        tapRiderLand(driver, you, land)
        driver.castSpell(you, commanderId).error shouldBe null
        driver.bothPass()
        plusOneCount(driver, commanderId) shouldBe 1

        driver.replaceState(
            driver.state
                .removeFromZone(ZoneKey(you, Zone.BATTLEFIELD), commanderId)
                .addToZone(ZoneKey(you, Zone.COMMAND), commanderId)
                .updateEntity(commanderId) { c -> c.without<CountersComponent>() }
        )

        driver.untapPermanent(land)
        driver.putPermanentOnBattlefield(you, "Mountain")
        driver.putPermanentOnBattlefield(you, "Mountain")
        tapRiderLand(driver, you, land)
        driver.castSpell(you, commanderId).error shouldBe null
        driver.bothPass()

        plusOneCount(driver, commanderId) shouldBe 2
        driver.state.getEntity(commanderId)?.get<CommanderComponent>()?.castsFromCommandZone shouldBe 2
    }

    test("a non-commander spell paid with rider mana gets no extra counters") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putPermanentOnBattlefield(you, "Test Opal Rider Land")
        val creature = driver.putCardInHand(you, "Test Non Commander")
        tapRiderLand(driver, you, land)

        driver.castSpell(you, creature).error shouldBe null
        driver.bothPass()

        plusOneCount(driver, creature) shouldBe 0
    }

    test("commander cast without rider mana gets no extra counters") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(you, "Mountain")
        val commanderId = driver.state.getZone(ZoneKey(you, Zone.COMMAND)).single()

        driver.castSpell(you, commanderId).error shouldBe null
        driver.bothPass()

        plusOneCount(driver, commanderId) shouldBe 0
        driver.state.getEntity(commanderId)?.get<CommanderComponent>()?.castsFromCommandZone shouldBe 1
    }
})
