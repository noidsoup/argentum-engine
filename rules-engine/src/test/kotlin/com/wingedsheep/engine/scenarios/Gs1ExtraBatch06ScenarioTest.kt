package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * GS1 Extra batch 06 — Fire-Omen Crane (attack ping), Hardened-Scale Armor (+3/+3 aura),
 * Heavenly Qilin (attack grants flying).
 */
class Gs1ExtraBatch06ScenarioTest : ScenarioTestBase() {

    init {
        test("Hardened-Scale Armor: enchanted creature gets +3/+3") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Hardened-Scale Armor")
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withLandsOnBattlefield(1, "Forest", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            game.state.projectedState.getPower(bears) shouldBe 2
            game.state.projectedState.getToughness(bears) shouldBe 2

            game.castSpell(1, "Hardened-Scale Armor", targetId = bears).error shouldBe null
            game.resolveStack()

            withClue("Aura attaches and pumps to 5/5") {
                game.isOnBattlefield("Hardened-Scale Armor") shouldBe true
                game.state.projectedState.getPower(bears) shouldBe 5
                game.state.projectedState.getToughness(bears) shouldBe 5
            }
        }

        test("Fire-Omen Crane: attacks and deals 1 to an opposing creature") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Fire-Omen Crane", summoningSickness = false)
                .withCardOnBattlefield(2, "Savannah Lions") // 1/1
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val lions = game.findPermanent("Savannah Lions")!!

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Fire-Omen Crane" to 2)).error shouldBe null

            if (game.state.pendingDecision == null) game.resolveStack()
            if (game.state.pendingDecision != null) {
                game.selectTargets(listOf(lions)).error shouldBe null
            }
            game.resolveStack()

            withClue("1/1 Savannah Lions dies to 1 damage from attack trigger") {
                game.isOnBattlefield("Savannah Lions") shouldBe false
            }
        }

        test("Heavenly Qilin: attacks and grants flying to another creature you control") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Heavenly Qilin", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val bears = game.findPermanent("Grizzly Bears")!!
            game.state.projectedState.hasKeyword(bears, Keyword.FLYING) shouldBe false

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Heavenly Qilin" to 2)).error shouldBe null

            if (game.state.pendingDecision == null) game.resolveStack()
            if (game.state.pendingDecision != null) {
                game.selectTargets(listOf(bears)).error shouldBe null
            }
            game.resolveStack()

            withClue("Bears gain flying until end of turn") {
                game.state.projectedState.hasKeyword(bears, Keyword.FLYING) shouldBe true
            }
        }
    }
}
