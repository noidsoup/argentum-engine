package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Rivals' Duel (MOR #99) — Choose two target creatures that share no creature types.
 * Those creatures fight each other.
 */
class RivalsDuelScenarioTest : ScenarioTestBase() {

    init {
        context("Rivals' Duel") {

            test("a Bear and a Goblin fight when they share no creature types") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Rivals' Duel")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Goblin Guide")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Rivals' Duel"
                }
                val bear = game.findPermanent("Grizzly Bears")!!
                val goblin = game.findPermanent("Goblin Guide")!!

                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        spellId,
                        listOf(ChosenTarget.Permanent(bear), ChosenTarget.Permanent(goblin)),
                    ),
                )
                withClue("Bear vs Goblin is a legal target pair") { cast.error shouldBe null }
                game.resolveStack()

                withClue("both 2/2 creatures trade in the fight") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                    game.isInGraveyard(2, "Goblin Guide") shouldBe true
                }
            }

            test("two Bears cannot be targeted together") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Rivals' Duel")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Rivals' Duel"
                }
                val bear1 = game.state.getBattlefield(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                }
                val bear2 = game.state.getBattlefield(game.player2Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
                }

                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        spellId,
                        listOf(ChosenTarget.Permanent(bear1), ChosenTarget.Permanent(bear2)),
                    ),
                )

                withClue("two Bears share the Bear creature type") {
                    cast.error shouldNotBe null
                }
            }
        }
    }
}
