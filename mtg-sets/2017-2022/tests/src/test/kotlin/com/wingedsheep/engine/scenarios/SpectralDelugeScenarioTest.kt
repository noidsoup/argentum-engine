package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Spectral Deluge (KHC #7) — bounce opponent creatures with toughness at most your Island count.
 */
class SpectralDelugeScenarioTest : ScenarioTestBase() {

    init {
        context("Spectral Deluge") {

            test("bounces opponent creatures with toughness at or below island count") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Spectral Deluge")
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Wall of Frost") // 0/7 — toughness 7, stays
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withCardOnBattlefield(2, "Colossal Dreadmaw")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Spectral Deluge"
                }
                game.execute(CastSpell(game.player1Id, spellId, emptyList())).error shouldBe null
                game.resolveStack()

                fun onBattlefield(player: Int, name: String) = game.state.getBattlefield(
                    if (player == 1) game.player1Id else game.player2Id,
                ).any { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == name
                }
                fun inHand(player: Int, name: String) = game.state.getHand(
                    if (player == 1) game.player1Id else game.player2Id,
                ).any { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == name
                }

                withClue("X=5: 2/2 and 3/3 bounce; 6/6 and 0/7 stay") {
                    onBattlefield(2, "Grizzly Bears") shouldBe false
                    onBattlefield(2, "Hill Giant") shouldBe false
                    inHand(2, "Grizzly Bears") shouldBe true
                    inHand(2, "Hill Giant") shouldBe true
                    onBattlefield(2, "Colossal Dreadmaw") shouldBe true
                    onBattlefield(2, "Wall of Frost") shouldBe true
                }
            }

            test("leaves creatures you control on the battlefield") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Spectral Deluge")
                    .withLandsOnBattlefield(1, "Island", 6)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Hill Giant")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val spellId = game.state.getHand(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Spectral Deluge"
                }
                game.execute(CastSpell(game.player1Id, spellId, emptyList())).error shouldBe null
                game.resolveStack()

                fun onBattlefield(player: Int, name: String) = game.state.getBattlefield(
                    if (player == 1) game.player1Id else game.player2Id,
                ).any { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == name
                }

                withClue("own creature stays; opponent's bounces with X=6") {
                    onBattlefield(1, "Grizzly Bears") shouldBe true
                    onBattlefield(2, "Hill Giant") shouldBe false
                }
            }
        }
    }
}
