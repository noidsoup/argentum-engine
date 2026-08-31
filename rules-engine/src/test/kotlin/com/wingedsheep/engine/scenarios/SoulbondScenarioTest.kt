package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.PairedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Soulbond mechanic tests (CR 702.95) plus Spectral Gateguards / Tandem Lookout payoffs.
 */
class SoulbondScenarioTest : ScenarioTestBase() {

    init {
        context("Soulbond pairing (CR 702.95)") {

            test("self ETB may pair with another unpaired creature you control") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Spectral Gateguards")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Spectral Gateguards").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                // Creature resolves → optional soulbond ETB asks for a target (min 0).
                val targetDecision = game.state.pendingDecision
                targetDecision.shouldBeInstanceOf<SelectCardsDecision>()
                val bears = game.findPermanent("Grizzly Bears")!!
                game.selectCards(listOf(bears)).error shouldBe null
                game.resolveStack()

                val gateguards = game.findPermanent("Spectral Gateguards")!!
                withClue("both creatures should be symmetrically paired") {
                    game.state.getEntity(gateguards)?.get<PairedComponent>()?.partnerId shouldBe bears
                    game.state.getEntity(bears)?.get<PairedComponent>()?.partnerId shouldBe gateguards
                }
            }

            test("declining self-ETB soulbond leaves both unpaired") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Spectral Gateguards")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Spectral Gateguards").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                game.state.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
                game.skipSelection().error shouldBe null
                game.resolveStack()

                val gateguards = game.findPermanent("Spectral Gateguards")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state.getEntity(gateguards)?.get<PairedComponent>() shouldBe null
                game.state.getEntity(bears)?.get<PairedComponent>() shouldBe null
            }

            test("other creature ETB may pair with an unpaired soulbond permanent") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Spectral Gateguards")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                // Other-creature ETB is a no-target optional → YesNo.
                game.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true)
                game.resolveStack()

                val gateguards = game.findPermanent("Spectral Gateguards")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state.getEntity(gateguards)?.get<PairedComponent>()?.partnerId shouldBe bears
                game.state.getEntity(bears)?.get<PairedComponent>()?.partnerId shouldBe gateguards
            }

            test("leaving the battlefield unpaired the mate (CR 702.95e)") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Spectral Gateguards")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Doom Blade")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val gateguards = game.findPermanent("Spectral Gateguards")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state = game.state
                    .updateEntity(gateguards) { it.with(PairedComponent(bears)) }
                    .updateEntity(bears) { it.with(PairedComponent(gateguards)) }

                game.castSpell(1, "Doom Blade", gateguards).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("survivor should be unpaired after the mate leaves") {
                    game.state.getEntity(bears)?.get<PairedComponent>() shouldBe null
                }
            }
        }

        context("Spectral Gateguards") {

            test("while paired both creatures have vigilance") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Spectral Gateguards")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val gateguards = game.findPermanent("Spectral Gateguards")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state = game.state
                    .updateEntity(gateguards) { it.with(PairedComponent(bears)) }
                    .updateEntity(bears) { it.with(PairedComponent(gateguards)) }

                withClue("both should have vigilance while paired") {
                    game.state.projectedState.hasKeyword(gateguards, Keyword.VIGILANCE) shouldBe true
                    game.state.projectedState.hasKeyword(bears, Keyword.VIGILANCE) shouldBe true
                }

                game.state = game.state
                    .updateEntity(gateguards) { it.without<PairedComponent>() }
                    .updateEntity(bears) { it.without<PairedComponent>() }

                withClue("neither should have vigilance after unpair") {
                    game.state.projectedState.hasKeyword(gateguards, Keyword.VIGILANCE) shouldBe false
                    game.state.projectedState.hasKeyword(bears, Keyword.VIGILANCE) shouldBe false
                }
            }
        }

        context("Tandem Lookout") {

            test("while paired either creature drawing on damage to an opponent") {
                var builder = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Tandem Lookout", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Island") }
                val game = builder.build()

                val lookout = game.findPermanent("Tandem Lookout")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state = game.state
                    .updateEntity(lookout) { it.with(PairedComponent(bears)) }
                    .updateEntity(bears) { it.with(PairedComponent(lookout)) }

                val handBefore = game.handSize(1)

                // Deal combat damage with the partner (Bears) to the opponent.
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("partner dealing combat damage to opponent should draw a card") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }
        }
    }
}
