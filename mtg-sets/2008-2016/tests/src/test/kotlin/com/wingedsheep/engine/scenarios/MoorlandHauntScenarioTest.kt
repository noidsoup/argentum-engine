package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.isd.cards.MoorlandHaunt
import com.wingedsheep.sdk.core.Keyword
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Moorland Haunt (ISD #244) — tap for colorless; {W}{U}, tap, exile a creature from graveyard
 * to make a 1/1 white Spirit token with flying.
 */
class MoorlandHauntScenarioTest : ScenarioTestBase() {

    init {
        context("Moorland Haunt") {

            test("{W}{U}, {T}, exile a creature card from graveyard creates a 1/1 Spirit with flying") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Moorland Haunt")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Island", 1)
                    .withActivePlayer(1)
                    .build()

                val haunt = game.findPermanent("Moorland Haunt")!!
                val abilityId = MoorlandHaunt.activatedAbilities[1].id
                val spiritsBefore = game.findPermanents("Spirit Token").size
                val graveyardBefore = game.graveyardSize(1)

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = haunt,
                        abilityId = abilityId,
                    ),
                )
                withClue("activation should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                withClue("the creature card was exiled from the graveyard") {
                    game.graveyardSize(1) shouldBe graveyardBefore - 1
                }
                withClue("a Spirit token was created") {
                    game.findPermanents("Spirit Token").size shouldBe spiritsBefore + 1
                }
                val spirit = game.findPermanents("Spirit Token").last()
                withClue("the Spirit token has flying") {
                    game.state.projectedState.hasKeyword(spirit, Keyword.FLYING) shouldBe true
                }
                withClue("the Spirit token is 1/1") {
                    game.state.projectedState.getPower(spirit) shouldBe 1
                    game.state.projectedState.getToughness(spirit) shouldBe 1
                }
            }
        }
    }
}
