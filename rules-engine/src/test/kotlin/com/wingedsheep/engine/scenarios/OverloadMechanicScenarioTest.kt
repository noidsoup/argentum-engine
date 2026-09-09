package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Overload (CR 702.95) — an alternative casting cost that replaces the spell's printed effect with
 * the overload variant at cast time. Vandalblast-shaped reference: single-target destroy vs destroy
 * each artifact you don't control.
 */
class OverloadMechanicScenarioTest : ScenarioTestBase() {

    private val testOverloadSorcery = card("Test Overload Sorcery") {
        manaCost = "{R}"
        typeLine = "Sorcery"
        keywordAbility(KeywordAbility.overload("{3}{R}"))
        spell {
            val targetArtifact = target(
                "target artifact",
                TargetPermanent(filter = TargetFilter.Artifact),
            )
            effect = Effects.Move(targetArtifact, com.wingedsheep.sdk.core.Zone.GRAVEYARD, byDestruction = true)
            overloadEffect = Effects.DestroyAll(GameObjectFilter.Artifact.opponentControls())
        }
    }

    private val testOverloadWitness = card("Test Overload Witness") {
        manaCost = "{1}"
        typeLine = "Creature — Human"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.youCastSpell(requires = setOf(SpellCastPredicate.WasOverloaded))
            effect = Effects.GainLife(3)
        }
    }

    init {
        cardRegistry.register(listOf(testOverloadSorcery, testOverloadWitness))

        context("printed cast — target one artifact") {
            test("destroys only the chosen artifact") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Test Overload Sorcery")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(1, "Sol Ring")
                    .withCardOnBattlefield(2, "Sol Ring")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val opponentRing = game.findPermanents("Sol Ring").single { entityId ->
                    game.state.projectedState.getController(entityId) == game.player2Id
                }
                val ownRing = game.findPermanents("Sol Ring").single { entityId ->
                    game.state.projectedState.getController(entityId) == game.player1Id
                }
                val cast = game.castSpell(1, "Test Overload Sorcery", targetId = opponentRing)
                withClue("Printed cast should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Opponent's Sol Ring is destroyed") {
                    game.state.getBattlefield().contains(opponentRing) shouldBe false
                }
                withClue("Caster's Sol Ring survives the single-target mode") {
                    game.state.getBattlefield().contains(ownRing) shouldBe true
                }
            }
        }

        context("overload cast — replace printed effect") {
            test("destroys each artifact the caster does not control and spares their own") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Test Overload Sorcery")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardOnBattlefield(1, "Sol Ring")
                    .withCardOnBattlefield(2, "Sol Ring")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpellWithOverload(1, "Test Overload Sorcery")
                withClue("Overload cast should succeed without targets: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val ownRing = game.findPermanents("Sol Ring").single { entityId ->
                    game.state.projectedState.getController(entityId) == game.player1Id
                }
                withClue("Opponent's Sol Ring is destroyed by the overload sweep") {
                    game.findPermanents("Sol Ring").count { entityId ->
                        game.state.projectedState.getController(entityId) == game.player2Id
                    } shouldBe 0
                }
                withClue("Caster's Sol Ring survives the overload sweep") {
                    game.state.getBattlefield().contains(ownRing) shouldBe true
                }
            }
        }

        context("SpellCastPredicate.WasOverloaded") {
            test("fires only when the spell was cast for its overload cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Test Overload Sorcery")
                    .withCardOnBattlefield(1, "Test Overload Witness")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardOnBattlefield(2, "Sol Ring")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val castOverload = game.castSpellWithOverload(1, "Test Overload Sorcery")
                withClue("Overload cast: ${castOverload.error}") {
                    castOverload.error shouldBe null
                }
                game.resolveStack()

                withClue("Overload trigger grants 3 life") {
                    game.getLifeTotal(1) shouldBe 23
                }
            }

            test("does not fire on a normal cast of the same spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Test Overload Sorcery")
                    .withCardOnBattlefield(1, "Test Overload Witness")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Sol Ring")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val opponentRing = game.findPermanents("Sol Ring").single { entityId ->
                    game.state.projectedState.getController(entityId) == game.player2Id
                }
                val cast = game.castSpell(1, "Test Overload Sorcery", targetId = opponentRing)
                withClue("Printed cast: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("WasOverloaded trigger does not fire on the printed cast") {
                    game.getLifeTotal(1) shouldBe 20
                }
            }
        }
    }
}
