package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.rav.cards.BrambleElemental
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Bramble Elemental (RAV #154) — {3}{G}{G} Creature — Elemental 4/4.
 *
 * Whenever an Aura becomes attached to this creature, create two 1/1 green Saproling creature tokens.
 */
class BrambleElementalScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    private val testAura = card("Test Bramble Aura") {
        manaCost = "{G}"
        colorIdentity = "G"
        typeLine = "Enchantment — Aura"
        oracleText = "Enchant creature"
        auraTarget = Targets.Creature
        metadata { rarity = Rarity.COMMON; collectorNumber = "1" }
    }

    init {
        cardRegistry.register(BrambleElemental)
        cardRegistry.register(testAura)

        context("Bramble Elemental") {

            fun saprolingTokens(game: TestGame, playerId: EntityId = game.player1Id): List<EntityId> =
                game.state.getZone(playerId, Zone.BATTLEFIELD).filter { id ->
                    game.state.getEntity(id)?.get<TokenComponent>() != null &&
                        game.state.projectedState.getSubtypes(id).any { it.equals("Saproling", ignoreCase = true) }
                }

            test("aura attaching creates two 1/1 green Saproling tokens") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Bramble Elemental")
                    .withCardInHand(1, "Test Bramble Aura")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elemental = game.findPermanent("Bramble Elemental")!!
                game.castSpell(1, "Test Bramble Aura", elemental).error shouldBe null
                game.resolveStack() // aura ETB attached
                game.resolveStack() // attach trigger

                val tokens = saprolingTokens(game)
                withClue("two Saproling tokens are created") {
                    tokens.size shouldBe 2
                }
                tokens.forEach { tokenId ->
                    projector.getProjectedPower(game.state, tokenId) shouldBe 1
                    projector.getProjectedToughness(game.state, tokenId) shouldBe 1
                }
            }
        }
    }
}
