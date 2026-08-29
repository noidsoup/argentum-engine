package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Vela the Night-Clad (PC2) — leave-the-battlefield drain.
 */
class VelaTheNightCladScenarioTest : ScenarioTestBase() {

    private val destroyCreature = card("Test Destroy Creature") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Destroy target creature."
        spell {
            val target = target("target", Targets.Creature)
            effect = Effects.Destroy(target)
        }
    }

    init {
        cardRegistry.register(destroyCreature)

        context("Vela the Night-Clad") {
            test("when a creature you control leaves the battlefield each opponent loses 1 life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Vela the Night-Clad")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Test Destroy Creature")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val opponentLifeBefore = game.getLifeTotal(2)
                val bear = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Test Destroy Creature", targetId = bear).error shouldBe null
                game.resolveStack()

                withClue("opponent loses 1 life") {
                    game.getLifeTotal(2) shouldBe opponentLifeBefore - 1
                }
            }

            test("when Vela leaves the battlefield each opponent loses 1 life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Vela the Night-Clad")
                    .withCardInHand(1, "Test Destroy Creature")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val opponentLifeBefore = game.getLifeTotal(2)
                val vela = game.findPermanent("Vela the Night-Clad")!!

                game.castSpell(1, "Test Destroy Creature", targetId = vela).error shouldBe null
                game.resolveStack()

                withClue("opponent loses 1 life when Vela leaves") {
                    game.getLifeTotal(2) shouldBe opponentLifeBefore - 1
                }
            }
        }
    }
}
