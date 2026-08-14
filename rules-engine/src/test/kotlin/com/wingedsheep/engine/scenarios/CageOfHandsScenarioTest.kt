package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Cage of Hands (CHK #3) — {2}{W} Enchantment — Aura.
 *
 * "Enchant creature
 *  Enchanted creature can't attack or block.
 *  {1}{W}: Return this Aura to its owner's hand."
 *
 * The self-bounce is the whole point of the card: it lets you re-lock a different creature later, so
 * these tests pin both halves of the lock and that returning the Aura releases it.
 */
class CageOfHandsScenarioTest : ScenarioTestBase() {

    init {
        context("Cage of Hands") {

            test("the enchanted creature can't attack") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Cage of Hands")
                    .withCardAttachedTo(1, "Cage of Hands", "Grizzly Bears")
                    .withActivePlayer(2)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val attack = game.declareAttackers(mapOf("Grizzly Bears" to 1))
                withClue("a caged creature cannot be declared as an attacker") {
                    attack.error shouldNotBe null
                }
            }

            test("the enchanted creature can't block") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Savannah Lions")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Cage of Hands")
                    .withCardAttachedTo(1, "Cage of Hands", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Savannah Lions" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                val block = game.declareBlockers(mapOf("Grizzly Bears" to listOf("Savannah Lions")))
                withClue("a caged creature cannot be declared as a blocker") {
                    block.error shouldNotBe null
                }
            }

            test("an unenchanted creature is unaffected — the lock is scoped to the attached host") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Savannah Lions")
                    .withCardOnBattlefield(1, "Cage of Hands")
                    .withCardAttachedTo(1, "Cage of Hands", "Grizzly Bears")
                    .withActivePlayer(2)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val attack = game.declareAttackers(mapOf("Savannah Lions" to 1))
                withClue("the creature without the Aura attacks normally") {
                    attack.error shouldBe null
                }
            }

            test("{1}{W} returns the Aura to its owner's hand and frees the creature") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Cage of Hands")
                    .withCardAttachedTo(1, "Cage of Hands", "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cage = game.findPermanent("Cage of Hands")!!
                val bounce = cardRegistry.getCard("Cage of Hands")!!.activatedAbilities[0].id
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = cage, abilityId = bounce)
                ).error shouldBe null
                game.resolveStack()

                withClue("the Aura left the battlefield for its owner's hand") {
                    game.isOnBattlefield("Cage of Hands") shouldBe false
                    game.isInHand(1, "Cage of Hands") shouldBe true
                }
                withClue("the freed creature is still around") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
