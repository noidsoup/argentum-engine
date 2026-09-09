package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.core.CombatResolutionResponse
import com.wingedsheep.engine.core.DamageEdgeAmount
import com.wingedsheep.engine.core.OrderObjectsDecision
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.PassPriority
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.combat.PlayerAttackedPlayersThisCombatComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PreventDamage
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.engine.state.components.player.AdditionalPhasesComponent
import com.wingedsheep.engine.state.components.player.ExtraPhaseKind
import com.wingedsheep.engine.state.components.player.QueuedPhase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Melee (CR 702.121) and [PreventionScope.NoncombatOnly] recipient shields — primitives for
 * Drogskol Reinforcements and the broader melee card family.
 */
class MeleeKeywordTest : FunSpec({

    val projector = StateProjector()

    fun meleeCreature(name: String, power: Int = 1, toughness: Int = 1): CardDefinition =
        CardDefinition.creature(
            name = name,
            manaCost = ManaCost.parse("{2}{W}"),
            subtypes = setOf(Subtype("Spirit"), Subtype("Soldier")),
            power = power,
            toughness = toughness,
            keywords = setOf(Keyword.MELEE),
        )

    fun vanilla(name: String): CardDefinition =
        CardDefinition.creature(
            name = name,
            manaCost = ManaCost.parse("{1}{W}"),
            subtypes = setOf(Subtype("Soldier")),
            power = 2,
            toughness = 2,
        )

    val spiritMeleeLord = card("Test Spirit Melee Lord") {
        manaCost = "{3}{W}"
        typeLine = "Creature — Spirit"
        power = 2
        toughness = 2
        staticAbility {
            ability = GrantKeyword(
                Keyword.MELEE,
                GroupFilter.OtherCreaturesYouControl.withSubtype("Spirit"),
            )
        }
    }

    val noncombatSpiritShield = card("Test Spirit Noncombat Shield") {
        manaCost = "{2}{W}"
        typeLine = "Enchantment"
        oracleText = "Prevent all noncombat damage that would be dealt to Spirits you control."
        replacementEffect(
            PreventDamage(
                appliesTo = EventPattern.DamageEvent(
                    recipient = RecipientFilter.Matching(
                        GameObjectFilter.Creature.youControl().withSubtype("Spirit"),
                    ),
                    damageType = DamageType.NonCombat,
                ),
            ),
        )
    }

    fun resolveThroughCombat(driver: GameTestDriver) {
        var guard = 0
        while (driver.currentStep != Step.POSTCOMBAT_MAIN && guard++ < 300) {
            when (val decision = driver.state.pendingDecision) {
                is OrderObjectsDecision ->
                    driver.submitDecision(decision.playerId, OrderedResponse(decision.id, decision.objects))
                is CombatResolutionDecision -> {
                    val edges = decision.edges.map { DamageEdgeAmount(it.id, it.amount) }
                    driver.submitDecision(decision.playerId, CombatResolutionResponse(decision.id, edges))
                }
                null -> {
                    val priority = driver.state.priorityPlayerId ?: break
                    driver.submit(PassPriority(priority))
                }
                else -> error("Unexpected decision: ${decision::class.simpleName}")
            }
            if (driver.state.gameOver) break
        }
    }

    fun initThreePlayer(driver: GameTestDriver): List<EntityId> {
        driver.registerCards(TestCards.all)
        return driver.initMultiplayer(
            decks = List(3) { Deck.of("Plains" to 40) },
            skipMulligans = true,
            startingPlayer = 0,
        )
    }

    test("melee grants +1/+1 for each opponent attacked this combat when attacking second opponent") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(meleeCreature("Test Melee Raider")))
        val players = initThreePlayer(driver)
        val you = players[0]
        val oppA = players[1]
        val oppB = players[2]

        val scout = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        val raider = driver.putCreatureOnBattlefield(you, "Test Melee Raider")
        driver.removeSummoningSickness(scout)
        driver.removeSummoningSickness(raider)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, mapOf(scout to oppA, raider to oppB))
        resolveThroughCombat(driver)

        val projected = projector.project(driver.state)
        projected.getPower(raider) shouldBe 3 // 1 base + 2 opponents attacked this combat
        projected.getToughness(raider) shouldBe 3
    }

    test("melee count resets between combats in the same turn") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(meleeCreature("Test Melee Solo")))
        val players = initThreePlayer(driver)
        val you = players[0]
        val oppA = players[1]

        val solo = driver.putCreatureOnBattlefield(you, "Test Melee Solo")
        driver.removeSummoningSickness(solo)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, mapOf(solo to oppA))
        resolveThroughCombat(driver)

        var projected = projector.project(driver.state)
        projected.getPower(solo) shouldBe 2 // +1/+1 for one opponent

        // Extra combat with only one opponent attacked again — should be +1/+1, not stacked from prior combat.
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.replaceState(
            driver.state.updateEntity(you) {
                it.with(AdditionalPhasesComponent(listOf(QueuedPhase(ExtraPhaseKind.COMBAT))))
            },
        )
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, mapOf(solo to oppA))
        resolveThroughCombat(driver)

        projected = projector.project(driver.state)
        projected.getPower(solo) shouldBe 2
    }

    test("melee lord with excludeSelf does not double-count its own printed melee") {
        val drogskolShape = card("Test Drogskol Shape") {
            manaCost = "{3}{W}"
            typeLine = "Creature — Spirit Soldier"
            power = 2
            toughness = 2
            keywords(Keyword.MELEE)
            staticAbility {
                ability = GrantKeyword(
                    Keyword.MELEE,
                    GroupFilter(
                        GameObjectFilter.Creature.withSubtype(Subtype.SPIRIT).youControl(),
                        excludeSelf = true,
                    ),
                )
            }
        }
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(drogskolShape))
        val players = initThreePlayer(driver)
        val you = players[0]
        val oppA = players[1]

        val lord = driver.putCreatureOnBattlefield(you, "Test Drogskol Shape")
        driver.removeSummoningSickness(lord)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, mapOf(lord to oppA))
        resolveThroughCombat(driver)

        val projected = projector.project(driver.state)
        projected.getPower(lord) shouldBe 3 // 2 base + 1 for one opponent, not doubled by its own lord line
    }

    test("granted melee on another Spirit triggers when that Spirit attacks") {
        val driver = GameTestDriver()
        val spiritScout = CardDefinition.creature(
            name = "Test Spirit Scout",
            manaCost = ManaCost.parse("{1}{W}"),
            subtypes = setOf(Subtype("Spirit"), Subtype("Soldier")),
            power = 2,
            toughness = 2,
        )
        driver.registerCards(TestCards.all + listOf(spiritScout, spiritMeleeLord))
        val players = initThreePlayer(driver)
        val you = players[0]
        val oppA = players[1]
        val oppB = players[2]

        driver.putCreatureOnBattlefield(you, "Test Spirit Melee Lord")
        val scout = driver.putCreatureOnBattlefield(you, "Test Spirit Scout")
        driver.removeSummoningSickness(scout)

        // Attack oppA with a different creature first so the scout's melee sees two opponents.
        val helper = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        driver.removeSummoningSickness(helper)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, mapOf(helper to oppA, scout to oppB))
        resolveThroughCombat(driver)

        val projected = projector.project(driver.state)
        projected.getPower(scout) shouldBe 4 // 2 base + 2 for two opponents attacked this combat
    }

    test("noncombat-only group shield prevents burn but not combat damage to Spirits") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(noncombatSpiritShield, meleeCreature("Test Spirit")))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        driver.putPermanentOnBattlefield(you, "Test Spirit Noncombat Shield")
        val spirit = driver.putCreatureOnBattlefield(you, "Test Spirit") // 1/1 Spirit
        val attacker = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears") // 2/2
        driver.removeSummoningSickness(attacker)

        driver.giveMana(you, Color.RED, 1)
        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.castSpellWithTargets(you, bolt, listOf(ChosenTarget.Permanent(spirit)))
        driver.bothPass()
        while (driver.state.stack.isNotEmpty() && !driver.isPaused) driver.bothPass()
        driver.state.getBattlefield().contains(spirit) shouldBe true

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(opponent, mapOf(attacker to you))
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(you, mapOf(spirit to listOf(attacker)))
        resolveThroughCombat(driver)

        driver.state.getBattlefield().contains(spirit) shouldBe false // 2 combat damage kills the 1/1
    }

    test("combat-scoped attacked-opponent tally is cleared at end of combat") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(vanilla("Test Scout")))
        val players = initThreePlayer(driver)
        val you = players[0]
        val oppA = players[1]

        val scout = driver.putCreatureOnBattlefield(you, "Test Scout")
        driver.removeSummoningSickness(scout)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(you, mapOf(scout to oppA))
        resolveThroughCombat(driver)

        driver.state.getEntity(you)?.has<PlayerAttackedPlayersThisCombatComponent>() shouldBe false
    }
})
