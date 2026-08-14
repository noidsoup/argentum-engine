package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Kazandu Refuge (ZEN #217) — Land.
 *
 * "This land enters tapped.
 *  When this land enters, you gain 1 life.
 *  {T}: Add {R} or {G}."
 *
 * One of the Zendikar "refuge" duals: the tapped entry is a replacement effect while the life gain
 * is a separate ETB trigger, so the two are asserted independently, along with each mana mode.
 */
class KazanduRefugeScenarioTest : ScenarioTestBase() {

    init {
        context("Kazandu Refuge") {

            test("enters tapped and gains 1 life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Kazandu Refuge")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lifeBefore = game.getLifeTotal(1)
                val refuge = game.findCardsInHand(1, "Kazandu Refuge").first()
                game.execute(PlayLand(game.player1Id, refuge)).error shouldBe null

                withClue("the replacement effect puts it onto the battlefield tapped") {
                    game.state.getEntity(refuge)?.has<TappedComponent>() shouldBe true
                }

                game.resolveStack()
                withClue("the ETB trigger gains exactly 1 life") {
                    game.getLifeTotal(1) shouldBe lifeBefore + 1
                }
            }

            test("{T}: Add {R}") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kazandu Refuge", tapped = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val refuge = game.findPermanent("Kazandu Refuge")!!
                val red = cardRegistry.getCard("Kazandu Refuge")!!.activatedAbilities[0].id
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = refuge, abilityId = red)
                ).error shouldBe null

                withClue("taps for red") {
                    game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.red shouldBe 1
                    game.state.getEntity(refuge)?.has<TappedComponent>() shouldBe true
                }
            }

            test("{T}: Add {G}") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kazandu Refuge", tapped = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val refuge = game.findPermanent("Kazandu Refuge")!!
                val green = cardRegistry.getCard("Kazandu Refuge")!!.activatedAbilities[1].id
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = refuge, abilityId = green)
                ).error shouldBe null

                withClue("taps for green") {
                    game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.green shouldBe 1
                    game.state.getEntity(refuge)?.has<TappedComponent>() shouldBe true
                }
            }
        }
    }
}
