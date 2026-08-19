package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * PC2 Extra compose batch: Fusion Elemental, Brindle Shoat, Silhana Ledgewalker,
 * Jwar Isle Refuge, Selesnya Sanctuary, Ondu Giant.
 */
class Pc2ExtraComposeScenarioTest : ScenarioTestBase() {

    init {
        test("Fusion Elemental is an 8/8") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Fusion Elemental")
                .build()

            val elemental = game.findPermanent("Fusion Elemental")!!
            game.state.projectedState.getPower(elemental) shouldBe 8
            game.state.projectedState.getToughness(elemental) shouldBe 8
        }

        test("Brindle Shoat: dying creates a 3/3 green Boar token") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Brindle Shoat")
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val shoat = game.findPermanent("Brindle Shoat")!!
            game.castSpell(1, "Lightning Bolt", shoat).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Brindle Shoat") shouldBe false
            game.resolveStack()

            val token = game.findPermanent("Boar Token") ?: game.findPermanent("Boar")
            withClue("dies trigger makes a 3/3 Boar") {
                token shouldNotBe null
                game.state.projectedState.getPower(token!!) shouldBe 3
                game.state.projectedState.getToughness(token) shouldBe 3
            }
        }

        test("Silhana Ledgewalker has hexproof") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Silhana Ledgewalker")
                .build()

            val walker = game.findPermanent("Silhana Ledgewalker")!!
            game.state.projectedState.hasKeyword(walker, Keyword.HEXPROOF) shouldBe true
        }

        context("Jwar Isle Refuge") {
            test("enters tapped and gains 1 life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Jwar Isle Refuge")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lifeBefore = game.getLifeTotal(1)
                val refuge = game.findCardsInHand(1, "Jwar Isle Refuge").first()
                game.execute(PlayLand(game.player1Id, refuge)).error shouldBe null

                game.state.getEntity(refuge)?.has<TappedComponent>() shouldBe true
                game.resolveStack()
                game.getLifeTotal(1) shouldBe lifeBefore + 1
            }

            test("{T}: Add {U}") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Jwar Isle Refuge", tapped = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val refuge = game.findPermanent("Jwar Isle Refuge")!!
                val blue = cardRegistry.getCard("Jwar Isle Refuge")!!.activatedAbilities[0].id
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = refuge, abilityId = blue)
                ).error shouldBe null

                game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.blue shouldBe 1
            }
        }

        test("Selesnya Sanctuary enters tapped and bounces a land") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withLandsOnBattlefield(1, "Forest", 1)
                .withCardInHand(1, "Selesnya Sanctuary")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val forest = game.findPermanent("Forest")!!
            val sanctuary = game.findCardsInHand(1, "Selesnya Sanctuary").first()
            game.execute(PlayLand(game.player1Id, sanctuary)).error shouldBe null
            game.state.getEntity(sanctuary)?.has<TappedComponent>() shouldBe true

            game.selectTargets(listOf(forest)).error shouldBe null
            game.resolveStack()

            withClue("chosen land returns to hand") {
                game.isOnBattlefield("Forest") shouldBe false
                game.isInHand(1, "Forest") shouldBe true
            }
            game.isOnBattlefield("Selesnya Sanctuary") shouldBe true
        }

        test("Ondu Giant ETB may fetch a basic land onto the battlefield tapped") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Ondu Giant")
                .withLandsOnBattlefield(1, "Forest", 4)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val island = game.state.getLibrary(game.player1Id).first { id ->
                game.state.getEntity(id)?.get<CardComponent>()?.name == "Island"
            }
            val bears = game.state.getLibrary(game.player1Id).first { id ->
                game.state.getEntity(id)?.get<CardComponent>()?.name == "Grizzly Bears"
            }

            game.castSpell(1, "Ondu Giant").error shouldBe null
            game.resolveStack()

            withClue("the ETB is a 'you may'") {
                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
            }
            game.answerYesNo(true).error shouldBe null

            val search = game.getPendingDecision()
            search.shouldBeInstanceOf<SelectCardsDecision>()
            search.options shouldContain island
            search.options shouldNotContain bears
            game.selectCards(listOf(island)).error shouldBe null
            game.resolveStack()

            game.isOnBattlefield("Island") shouldBe true
            game.state.getEntity(game.findPermanent("Island")!!)?.has<TappedComponent>() shouldBe true
        }
    }
}
