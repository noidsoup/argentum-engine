package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ForetellCard
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PipelineState
import com.wingedsheep.engine.handlers.effects.library.MakeForetoldExecutor
import com.wingedsheep.engine.mechanics.foretell.ForetellCastCosts
import com.wingedsheep.engine.mechanics.mana.ForetellSetupCostReducer
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.ForetellCastOptionsComponent
import com.wingedsheep.engine.state.components.identity.ForetoldComponent
import com.wingedsheep.engine.state.components.identity.PlayWithFixedAlternativeManaCostComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ForetellSetupCostGating
import com.wingedsheep.sdk.scripting.ForetellSetupCostTarget
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ModifyForetellSetupCost
import com.wingedsheep.sdk.scripting.effects.ForetellCostSpec
import com.wingedsheep.sdk.scripting.effects.MakeForetoldEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe

/**
 * Mechanic tests for F-FORETELL-SETUP:
 * - [ModifyForetellSetupCost] / `ForetellSetupCostReducer` (Ranar: first foretell each turn costs {0})
 * - [MakeForetoldEffect] / `MakeForetoldExecutor` (Ethereal Valkyrie: exile from hand → foretold
 *   with mana cost − {2} generic)
 */
class ForetellSetupMechanicTest : FunSpec({

    val ForetellableSorcery = card("Foretell Setup Test Sorcery") {
        manaCost = "{4}{R}{R}"
        typeLine = "Sorcery"
        oracleText = "Foretell {2}{R}"
        keywordAbility(KeywordAbility.foretell("{2}{R}"))
        spell { effect = com.wingedsheep.sdk.dsl.Effects.DrawCards(1) }
    }

    val ForetellDiscountLord = card("Foretell Discount Lord Test") {
        manaCost = "{2}{W}{U}"
        typeLine = "Legendary Creature — Spirit"
        power = 2
        toughness = 3
        staticAbility {
            ability = ModifyForetellSetupCost(
                target = ForetellSetupCostTarget.YouForetellFromHand,
                modification = CostModification.ReduceGeneric(2),
                gating = ForetellSetupCostGating.NthForetellPerTurn(1),
            )
        }
    }

    fun registry(): CardRegistry {
        val r = CardRegistry()
        r.register(TestCards.all)
        r.register(ForetellableSorcery)
        r.register(ForetellDiscountLord)
        return r
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(ForetellableSorcery, ForetellDiscountLord))
        return d
    }

    fun setupWithLord(): Pair<GameTestDriver, com.wingedsheep.sdk.model.EntityId> {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 20), startingLife = 20)
        val active = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putCreatureOnBattlefield(active, ForetellDiscountLord.name)
        return d to active
    }

    test("first foretell each turn costs {0} with Ranar-style static") {
        val (d, active) = setupWithLord()
        val cardId = d.putCardInHand(active, ForetellableSorcery.name)
        d.submitSuccess(ForetellCard(active, cardId))
        d.state.foretellCountThisTurnByPlayer[active] shouldBe 1

        val secondCardId = d.putCardInHand(active, ForetellableSorcery.name)
        d.submit(ForetellCard(active, secondCardId)).isSuccess.shouldBeFalse()
    }

    test("second foretell setup cost in the same turn is {2}") {
        val (d, active) = setupWithLord()
        val reducer = ForetellSetupCostReducer(d.cardRegistry)
        val afterFirst = d.state.copy(
            foretellCountThisTurnByPlayer = mapOf(active to 1)
        )
        reducer.effectiveSetupCost(afterFirst, active).toString() shouldBe "{2}"
    }

    test("MakeForetold stamps foretold state with mana cost minus two generic") {
        val (d, active) = setupWithLord()
        val cardId = d.putCardInHand(active, ForetellableSorcery.name)
        var state = d.state
        state = state.removeFromZone(ZoneKey(active, Zone.HAND), cardId)
        state = state.addToZone(ZoneKey(active, Zone.EXILE), cardId)
        state = state.updateEntity(cardId) { it.with(FaceDownComponent) }

        val executor = MakeForetoldExecutor(d.cardRegistry)
        val result = executor.execute(
            state,
            MakeForetoldEffect(from = "picked"),
            EffectContext(
                sourceId = null,
                controllerId = active,
                pipeline = PipelineState(storedCollections = mapOf("picked" to listOf(cardId))),
            ),
        )

        val exiled = result.state.getEntity(cardId)!!
        exiled.get<ForetoldComponent>() shouldBe ForetoldComponent(active, state.turnNumber)
        exiled.get<FaceDownComponent>() shouldBe FaceDownComponent
        ForetellCastCosts.allCosts(exiled, active)
            .map { it.toString() }
            .toSet() shouldBe setOf("{2}{R}", "{2}{R}{R}")
    }

    test("card with foretell keyword plus MakeForetold exposes two distinct foretell costs") {
        val player = com.wingedsheep.sdk.model.EntityId.of("player-1")
        val keywordCost = ManaCost.parse("{2}{R}")
        val grantedCost = ForetellableSorcery.manaCost.reduceGeneric(2)
        val container = ComponentContainer()
            .with(PlayWithFixedAlternativeManaCostComponent(player, keywordCost))
            .with(ForetellCastOptionsComponent(player, listOf(grantedCost)))

        ForetellCastCosts.allCosts(container, player)
            .map { it.toString() }
            .toSet() shouldBe setOf("{2}{R}", "{2}{R}{R}")
    }
})
