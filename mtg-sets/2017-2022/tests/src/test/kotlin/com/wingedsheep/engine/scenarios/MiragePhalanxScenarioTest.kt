package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.PairedComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Mirage Phalanx (VOC #35) — soulbond + combat token copies with haste, no soulbond, exile EOC.
 */
class MiragePhalanxScenarioTest : ScenarioTestBase() {

    init {
        context("Mirage Phalanx") {

            test("while paired, each half creates a haste token copy at begin combat") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Mirage Phalanx", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val phalanx = game.findPermanent("Mirage Phalanx")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state = game.state
                    .updateEntity(phalanx) { it.with(PairedComponent(bears)) }
                    .updateEntity(bears) { it.with(PairedComponent(phalanx)) }

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                val phalanxCopies = game.findPermanents("Mirage Phalanx")
                val bearCopies = game.findPermanents("Grizzly Bears")
                withClue("each paired half mints one token copy of itself") {
                    phalanxCopies shouldHaveSize 2
                    bearCopies shouldHaveSize 2
                }

                val phalanxToken = phalanxCopies.first { it != phalanx }
                withClue("the copy is a token with haste and without soulbond") {
                    game.state.getEntity(phalanxToken)?.has<TokenComponent>() shouldBe true
                    game.state.projectedState.hasKeyword(phalanxToken, Keyword.HASTE) shouldBe true
                    game.state.projectedState.hasKeyword(phalanxToken, Keyword.SOULBOND) shouldBe false
                }
            }

            test("unpaired Phalanx creates no combat tokens") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Mirage Phalanx", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                withClue("soulbondPair scope is empty while unpaired") {
                    game.findPermanents("Mirage Phalanx") shouldHaveSize 1
                }
            }

            test("the combat token is exiled at end of combat") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Mirage Phalanx", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val phalanx = game.findPermanent("Mirage Phalanx")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state = game.state
                    .updateEntity(phalanx) { it.with(PairedComponent(bears)) }
                    .updateEntity(bears) { it.with(PairedComponent(phalanx)) }

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()
                game.findPermanents("Mirage Phalanx") shouldHaveSize 2

                game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                game.resolveStack()
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.resolveStack()

                withClue("only the original Phalanx remains after end of combat") {
                    game.findPermanents("Mirage Phalanx") shouldHaveSize 1
                    game.findPermanent("Mirage Phalanx") shouldNotBe null
                }
            }
        }
    }
}
