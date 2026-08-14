package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Predatory Urge (ZEN #175) — {3}{G} Enchantment — Aura.
 *
 * "Enchant creature
 *  Enchanted creature has '{T}: This creature deals damage equal to its power to target creature.
 *  That creature deals damage equal to its power to this creature.'"
 *
 * The point of these tests is that this is *not* a fight. The two damage clauses are separate and
 * sequential, which is observable when the enchanted creature targets itself: it takes its own
 * power twice (ruling 2009-10-01). A [com.wingedsheep.sdk.scripting.effects.FightEffect] would deal
 * that damage once.
 */
class PredatoryUrgeScenarioTest : ScenarioTestBase() {

    init {
        val grantedAbilityId = cardRegistry.getCard("Predatory Urge")!!
            .staticAbilities.filterIsInstance<GrantActivatedAbility>().first().ability.id

        context("Predatory Urge") {

            fun damageOn(game: TestGame, id: EntityId): Int =
                game.state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

            test("the enchanted creature and the target trade damage equal to their powers") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Centaur Courser")     // 3/3, the enchanted creature
                    .withCardAttachedTo(1, "Predatory Urge", "Centaur Courser")
                    .withCardOnBattlefield(2, "Force of Nature")     // 5/5
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                val forceOfNature = game.findPermanent("Force of Nature")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = courser,
                        abilityId = grantedAbilityId,
                        targets = listOf(ChosenTarget.Permanent(forceOfNature))
                    )
                )
                withClue("Activation should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()

                withClue("the target takes the enchanted creature's power (3)") {
                    damageOn(game, forceOfNature) shouldBe 3
                }
                withClue("the enchanted creature takes the target's power (5) and dies") {
                    game.findPermanent("Centaur Courser") shouldBe null
                    game.isInGraveyard(1, "Centaur Courser") shouldBe true
                }
            }

            test("targeting itself deals its power twice, not once") {
                // Armored Griffin is a 2/3: one hit of 2 leaves it alive, two hits kill it. That gap
                // is what separates the printed two-clause wording from a fight.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Armored Griffin")
                    .withCardAttachedTo(1, "Predatory Urge", "Armored Griffin")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val griffin = game.findPermanent("Armored Griffin")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = griffin,
                        abilityId = grantedAbilityId,
                        targets = listOf(ChosenTarget.Permanent(griffin))
                    )
                )
                withClue("A creature may target itself with this ability: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("2 + 2 = 4 damage on a 2/3 kills it; a single 2-damage hit would not") {
                    game.findPermanent("Armored Griffin") shouldBe null
                    game.isInGraveyard(1, "Armored Griffin") shouldBe true
                }
            }

            test("the ability is granted to the enchanted creature, not to the Aura") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardAttachedTo(1, "Predatory Urge", "Centaur Courser")
                    .withCardOnBattlefield(2, "Force of Nature")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                val aura = game.findPermanent("Predatory Urge")!!

                val actions = game.getLegalActions(1).filter { it.actionType == "ActivateAbility" }
                withClue("the enchanted creature offers the tap ability") {
                    actions.any { (it.action as? ActivateAbility)?.sourceId == courser } shouldBe true
                }
                withClue("the Aura itself does not") {
                    actions.none { (it.action as? ActivateAbility)?.sourceId == aura } shouldBe true
                }
            }

            test("the ability fizzles if its only target leaves before resolution") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardAttachedTo(1, "Predatory Urge", "Centaur Courser")
                    .withCardOnBattlefield(2, "Force of Nature")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                val forceOfNature = game.findPermanent("Force of Nature")!!

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = courser,
                        abilityId = grantedAbilityId,
                        targets = listOf(ChosenTarget.Permanent(forceOfNature))
                    )
                ).error shouldBe null

                // Remove the only target while the ability is still on the stack.
                game.state = game.state.removeFromZone(
                    ZoneKey(game.player2Id, Zone.BATTLEFIELD),
                    forceOfNature
                )
                game.resolveStack()

                withClue("with its only target gone the ability is countered — no damage comes back") {
                    game.findPermanent("Centaur Courser") shouldNotBe null
                    damageOn(game, courser) shouldBe 0
                }
            }

            test("source power uses LKI if the enchanted creature leaves before resolution") {
                // Ruling 2009-10-01: if the enchanted creature leaves before resolution, it still
                // deals damage equal to the power it last had on the battlefield. Tap-only cost —
                // no self-sacrifice snapshot — so activation must stamp source LKI unconditionally.
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Centaur Courser") // printed 3/3
                    .withCardAttachedTo(1, "Predatory Urge", "Centaur Courser")
                    .withCardOnBattlefield(2, "Force of Nature")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val courser = game.findPermanent("Centaur Courser")!!
                val forceOfNature = game.findPermanent("Force of Nature")!!

                // Buff to 4/4 so LKI (4) diverges from printed base power (3) without being
                // lethal to Force of Nature (5/5 in the test corpus) — a dead recipient would
                // make damageOn() read 0.
                game.state = game.state.updateEntity(courser) { c ->
                    c.with(CountersComponent().withAdded(CounterType.PLUS_ONE_PLUS_ONE, 1))
                }

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = courser,
                        abilityId = grantedAbilityId,
                        targets = listOf(ChosenTarget.Permanent(forceOfNature))
                    )
                ).error shouldBe null

                val onStack = game.state.getEntity(game.state.stack.last())
                    ?.get<com.wingedsheep.engine.state.components.stack.ActivatedAbilityOnStackComponent>()
                withClue("activation stamps source LKI including counters") {
                    onStack?.lastKnownSourceSnapshot?.power shouldBe 4
                }

                // Source leaves in response (tap paid; creature gone before resolve).
                game.state = game.state
                    .removeFromZone(ZoneKey(game.player1Id, Zone.BATTLEFIELD), courser)
                    .addToZone(ZoneKey(game.player1Id, Zone.GRAVEYARD), courser)
                game.resolveStack()

                withClue("deals last-known buffed power (4), not printed 3") {
                    game.findPermanent("Force of Nature") shouldNotBe null
                    damageOn(game, forceOfNature) shouldBe 4
                }
            }
        }
    }
}
