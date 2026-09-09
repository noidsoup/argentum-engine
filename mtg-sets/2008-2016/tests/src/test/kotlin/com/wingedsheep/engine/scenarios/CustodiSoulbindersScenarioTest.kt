package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.cns.cards.CustodiSoulbinders
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Custodi Soulbinders — enters with +1/+1 counters equal to other creatures on the battlefield;
 * {2}{W}, remove a counter: create a 1/1 white Spirit token with flying.
 */
class CustodiSoulbindersScenarioTest : ScenarioTestBase() {

    init {
        context("Custodi Soulbinders") {

            fun plusOneCounters(game: TestGame, name: String): Int =
                game.findPermanent(name)?.let { id ->
                    game.state.getEntity(id)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE)
                } ?: 0

            test("enters with counters equal to other creatures on the battlefield") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Custodi Soulbinders")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Elvish Warrior")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .build()

                game.castSpell(1, "Custodi Soulbinders").error shouldBe null
                game.resolveStack()

                withClue("two other creatures were on the battlefield") {
                    plusOneCounters(game, "Custodi Soulbinders") shouldBe 2
                }
                withClue("0/0 plus two counters is a 2/2") {
                    val soulbinders = game.findPermanent("Custodi Soulbinders")!!
                    game.state.projectedState.getPower(soulbinders) shouldBe 2
                    game.state.projectedState.getToughness(soulbinders) shouldBe 2
                }
            }

            test("{2}{W}, remove a counter: create a 1/1 white Spirit token with flying") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Custodi Soulbinders")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 7)
                    .build()

                game.castSpell(1, "Custodi Soulbinders").error shouldBe null
                game.resolveStack()

                val soulbinders = game.findPermanent("Custodi Soulbinders")!!
                val abilityId = CustodiSoulbinders.activatedAbilities.single().id
                val spiritsBefore = game.findPermanents("Spirit Token").size

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = soulbinders,
                        abilityId = abilityId,
                    ),
                )
                withClue("activation should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                withClue("the counter was spent") {
                    plusOneCounters(game, "Custodi Soulbinders") shouldBe 0
                }
                withClue("a Spirit token was created") {
                    game.findPermanents("Spirit Token").size shouldBe spiritsBefore + 1
                }
                val spirit = game.findPermanents("Spirit Token").last()
                withClue("the Spirit token has flying") {
                    game.state.projectedState.hasKeyword(spirit, Keyword.FLYING) shouldBe true
                }
            }
        }
    }
}
