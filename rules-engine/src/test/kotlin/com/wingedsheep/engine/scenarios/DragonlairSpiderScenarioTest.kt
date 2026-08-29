package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.pc2.cards.DragonlairSpider
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dragonlair Spider — Reach. Whenever an opponent casts a spell, create a 1/1 green Insect token.
 */
class DragonlairSpiderScenarioTest : FunSpec({

    val zap = card("Zap") {
        manaCost = "{0}"
        typeLine = "Instant"
        spell { effect = Effects.GainLife(1) }
    }

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(DragonlairSpider, zap))
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    fun resolveStack(d: GameTestDriver) {
        var guard = 0
        while ((d.state.stack.isNotEmpty() || d.pendingDecision != null) && guard++ < 12) {
            val decision = d.pendingDecision
            if (decision is YesNoDecision) d.submitYesNo(decision.playerId, true) else d.bothPass()
        }
    }

    fun insectTokens(d: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId): Int =
        d.getPermanents(player).count { id ->
            d.state.getEntity(id)?.get<com.wingedsheep.engine.state.components.identity.TokenComponent>() != null &&
                d.state.projectedState.getSubtypes(id)
                    .any { it.equals("Insect", ignoreCase = true) }
        }

    test("opponent casting a spell creates an Insect token") {
        val d = driver()
        val you = d.activePlayer!!
        val opp = d.getOpponent(you)

        d.putCreatureOnBattlefield(you, "Dragonlair Spider")
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.passPriority(you)

        d.castSpell(opp, d.putCardInHand(opp, "Zap")).error shouldBe null
        resolveStack(d)

        insectTokens(d, you) shouldBe 1
    }

    test("your own spell does not create a token") {
        val d = driver()
        val you = d.activePlayer!!

        d.putCreatureOnBattlefield(you, "Dragonlair Spider")
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.castSpell(you, d.putCardInHand(you, "Zap")).error shouldBe null
        resolveStack(d)

        insectTokens(d, you) shouldBe 0
    }
})
