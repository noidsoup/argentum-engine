package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.ori.cards.WhirlerRogue
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Whirler Rogue (ORI #83) — enters with two Thopter tokens; may tap two artifacts to make a
 * creature unblockable this turn.
 */
class WhirlerRogueScenarioTest : ScenarioTestBase() {

    private val activateAbilityId = WhirlerRogue.activatedAbilities.first().id

    init {
        context("Whirler Rogue") {
            test("enters the battlefield with two Thopter tokens") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Whirler Rogue")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Whirler Rogue").error shouldBe null
                game.resolveStack()

                withClue("the enters trigger makes exactly two 1/1 Thopter artifact tokens") {
                    game.findPermanents("Thopter Token").size shouldBe 2
                }
            }

            test("tapping two artifacts makes the chosen creature unblockable this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Whirler Rogue", summoningSickness = false)
                    .withCardOnBattlefield(1, "Sol Ring", summoningSickness = false)
                    .withCardOnBattlefield(1, "Commander's Sphere", summoningSickness = false)
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                val rogue = game.findPermanent("Whirler Rogue")!!
                val solRing = game.findPermanent("Sol Ring")!!
                val sphere = game.findPermanent("Commander's Sphere")!!
                val giant = game.findPermanent("Hill Giant")!!

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = rogue,
                        abilityId = activateAbilityId,
                        targets = listOf(ChosenTarget.Permanent(giant)),
                        costPayment = AdditionalCostPayment(tappedPermanents = listOf(solRing, sphere)),
                    ),
                ).error shouldBe null
                game.resolveStack()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Hill Giant" to 2)).error shouldBe null
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                withClue("the granted can't-be-blocked effect must stop Grizzly Bears from blocking") {
                    game.declareBlockers(mapOf("Grizzly Bears" to listOf("Hill Giant"))).error shouldNotBe null
                }
            }
        }
    }
}
