package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.jud.cards.NullmageAdvocate
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Nullmage Advocate (JUD #126).
 *
 * {T}: Return two target cards from an opponent's graveyard to their hand. Destroy target artifact
 * or enchantment.
 */
class NullmageAdvocateScenarioTest : ScenarioTestBase() {

    init {
        val abilityId = NullmageAdvocate.activatedAbilities.first().id

        context("Nullmage Advocate") {

            test("activated ability returns two opponent graveyard cards and destroys an enchantment") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Nullmage Advocate", summoningSickness = false)
                    .withCardInGraveyard(2, "Glory Seeker")
                    .withCardInGraveyard(2, "Hill Giant")
                    .withCardOnBattlefield(2, "Test Enchantment")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val advocate = game.findPermanent("Nullmage Advocate")!!
                val gy1 = game.findCardsInGraveyard(2, "Glory Seeker").first()
                val gy2 = game.findCardsInGraveyard(2, "Hill Giant").first()
                val enchantment = game.findPermanent("Test Enchantment")!!

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = advocate,
                        abilityId = abilityId,
                        targets = listOf(
                            ChosenTarget.Card(gy1, game.player2Id, Zone.GRAVEYARD),
                            ChosenTarget.Card(gy2, game.player2Id, Zone.GRAVEYARD),
                            ChosenTarget.Permanent(enchantment),
                        ),
                    ),
                ).error shouldBe null
                game.resolveStack()

                game.handSize(2) shouldBe 2
                game.findPermanent("Test Enchantment") shouldBe null
                game.graveyardSize(2) shouldBe 1 // destroyed enchantment only
            }

            test("cannot choose graveyard cards from two different graveyards") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Nullmage Advocate", summoningSickness = false)
                    .withCardInGraveyard(1, "Glory Seeker")
                    .withCardInGraveyard(2, "Hill Giant")
                    .withCardOnBattlefield(2, "Test Enchantment")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val advocate = game.findPermanent("Nullmage Advocate")!!
                val mine = game.findCardsInGraveyard(1, "Glory Seeker").first()
                val theirs = game.findCardsInGraveyard(2, "Hill Giant").first()
                val enchantment = game.findPermanent("Test Enchantment")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = advocate,
                        abilityId = abilityId,
                        targets = listOf(
                            ChosenTarget.Card(mine, game.player1Id, Zone.GRAVEYARD),
                            ChosenTarget.Card(theirs, game.player2Id, Zone.GRAVEYARD),
                            ChosenTarget.Permanent(enchantment),
                        ),
                    ),
                )
                withClue("two different graveyards must be rejected") {
                    result.error shouldNotBe null
                }
            }
        }
    }
}
