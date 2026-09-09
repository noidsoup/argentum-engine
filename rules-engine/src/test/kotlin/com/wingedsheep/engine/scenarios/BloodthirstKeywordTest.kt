package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.bloodthirst
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeywordToOwnSpells
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Mechanic-level tests for Bloodthirst N (CR 702.53).
 *
 * A card carrying `Bloodthirst 3` declares one keyword ability; the engine synthesizes the
 * enters-with replacement from [com.wingedsheep.sdk.scripting.Bloodthirst] at the entry seam.
 */
class BloodthirstKeywordTest : FunSpec({

    val bloodthirstBear = card("Bloodthirst Bear") {
        manaCost = "{2}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
        oracleText = "Bloodthirst 3"
        bloodthirst(3)
    }

    val plainVampire = card("Plain Vampire") {
        manaCost = "{1}{B}"
        typeLine = "Creature — Vampire"
        power = 1
        toughness = 1
    }

    val vampireLord = card("Vampire Lord") {
        manaCost = "{2}{B}"
        typeLine = "Creature — Vampire"
        power = 2
        toughness = 2
        oracleText = "Whenever you cast a Vampire creature spell, it gains bloodthirst 3."
        triggeredAbility {
            trigger = Triggers.YouCastSubtype(Subtype.VAMPIRE)
            effect = Effects.GrantKeywordToSpell(Keyword.BLOODTHIRST, keywordParameter = 3)
        }
    }

    val bloodthirstLord = card("Bloodthirst Lord") {
        manaCost = "{2}{R}"
        typeLine = "Creature — Warrior"
        power = 2
        toughness = 2
        oracleText = "Creature spells you cast have bloodthirst 2."
        staticAbility {
            ability = GrantKeywordToOwnSpells(
                keyword = Keyword.BLOODTHIRST,
                spellFilter = GameObjectFilter.Creature,
                keywordParameter = 2,
            )
        }
    }

    fun createDriver(extra: List<com.wingedsheep.sdk.model.CardDefinition> = emptyList()): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(bloodthirstBear, plainVampire, vampireLord, bloodthirstLord) + extra
        )
        driver.initMirrorMatch(deck = Deck.of("Forest" to 20, "Mountain" to 20))
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun plusOneCounters(driver: GameTestDriver, perm: EntityId): Int =
        driver.state.getEntity(perm)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    fun castAndResolve(
        driver: GameTestDriver,
        player: EntityId,
        cardName: String,
        targets: List<EntityId> = emptyList(),
    ) {
        driver.giveMana(player, Color.GREEN, 5)
        driver.giveMana(player, Color.BLACK, 5)
        driver.giveMana(player, Color.RED, 5)
        val cardId = driver.putCardInHand(player, cardName)
        val result = driver.submit(
            CastSpell(player, cardId, targets.map { ChosenTarget.Permanent(it) })
        )
        if (!result.isSuccess) throw AssertionError("cast of $cardName failed: ${result.error}")
        var passes = 0
        while (driver.stackSize > 0 && passes < 20) {
            driver.bothPass()
            passes++
        }
    }

    fun boltOpponent(driver: GameTestDriver, caster: EntityId, opponent: EntityId) {
        driver.giveMana(caster, Color.RED, 1)
        val bolt = driver.putCardInHand(caster, "Lightning Bolt")
        val result = driver.submit(
            CastSpell(caster, bolt, listOf(ChosenTarget.Player(opponent)))
        )
        if (!result.isSuccess) throw AssertionError("Lightning Bolt failed: ${result.error}")
        var passes = 0
        while (driver.stackSize > 0 && passes < 20) {
            driver.bothPass()
            passes++
        }
    }

    test("bloodthirst 3 enters with three +1/+1 counters when an opponent was dealt damage") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        boltOpponent(driver, you, opponent)
        castAndResolve(driver, you, "Bloodthirst Bear")
        val bear = driver.findPermanent(you, "Bloodthirst Bear")!!
        plusOneCounters(driver, bear) shouldBe 3
    }

    test("bloodthirst 3 enters without counters when no opponent was dealt damage") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        castAndResolve(driver, you, "Bloodthirst Bear")
        val bear = driver.findPermanent(you, "Bloodthirst Bear")!!
        plusOneCounters(driver, bear) shouldBe 0
    }

    test("life loss does not satisfy bloodthirst — only damage dealt counts") {
        val drain = card("Drain One") {
            manaCost = "{B}"
            typeLine = "Sorcery"
            spell {
                val foe = target("opponent", Targets.Opponent)
                effect = Effects.LoseLife(1, foe)
            }
        }
        val driver = createDriver(listOf(drain))
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        driver.giveMana(you, Color.BLACK, 1)
        val drainId = driver.putCardInHand(you, "Drain One")
        driver.submit(CastSpell(you, drainId, listOf(ChosenTarget.Player(opponent)))).isSuccess shouldBe true
        driver.bothPass()
        castAndResolve(driver, you, "Bloodthirst Bear")
        val bear = driver.findPermanent(you, "Bloodthirst Bear")!!
        plusOneCounters(driver, bear) shouldBe 0
    }

    test("a Vampire cast under a bloodlord trigger gains bloodthirst 3 for that resolution") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        castAndResolve(driver, you, "Vampire Lord")
        boltOpponent(driver, you, opponent)
        castAndResolve(driver, you, "Plain Vampire")
        val vampire = driver.findPermanent(you, "Plain Vampire")!!
        plusOneCounters(driver, vampire) shouldBe 3
    }

    test("GrantKeywordToOwnSpells grants bloodthirst to creature spells you cast") {
        val driver = createDriver()
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        castAndResolve(driver, you, "Bloodthirst Lord")
        boltOpponent(driver, you, opponent)
        castAndResolve(driver, you, "Plain Vampire")
        val vampire = driver.findPermanent(you, "Plain Vampire")!!
        plusOneCounters(driver, vampire) shouldBe 2
    }

    test("multiple bloodthirst instances stack") {
        val doubleBloodthirst = card("Double Bloodthirst Bear") {
            manaCost = "{3}{G}"
            typeLine = "Creature — Bear"
            power = 2
            toughness = 2
            keywordAbility(com.wingedsheep.sdk.scripting.KeywordAbility.bloodthirst(2))
            keywordAbility(com.wingedsheep.sdk.scripting.KeywordAbility.bloodthirst(1))
        }
        val driver = createDriver(listOf(doubleBloodthirst))
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)
        boltOpponent(driver, you, opponent)
        castAndResolve(driver, you, "Double Bloodthirst Bear")
        val bear = driver.findPermanent(you, "Double Bloodthirst Bear")!!
        plusOneCounters(driver, bear) shouldBe 3
    }
})
