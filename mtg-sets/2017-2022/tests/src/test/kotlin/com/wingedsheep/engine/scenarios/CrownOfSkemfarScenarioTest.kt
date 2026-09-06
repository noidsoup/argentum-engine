package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Crown of Skemfar (KHC #13) — enchanted creature gets +1/+1 for each Elf you control and has
 * reach; {2}{G} returns the Aura from the graveyard to its owner's hand.
 */
class CrownOfSkemfarScenarioTest : ScenarioTestBase() {

    init {
        context("Crown of Skemfar") {

            test("enchanted creature gets +1/+1 for each Elf you control and has reach") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Crown of Skemfar")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Elvish Warrior")
                    .withCardOnBattlefield(1, "Farhaven Elf")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Crown of Skemfar", bear).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("two Elves on the battlefield pump the enchanted Bear to 4/4") {
                    game.state.projectedState.getPower(bear) shouldBe 4
                    game.state.projectedState.getToughness(bear) shouldBe 4
                }
                withClue("enchanted creature has reach") {
                    game.state.projectedState.hasKeyword(bear, Keyword.REACH) shouldBe true
                }
            }

            test("{2}{G} returns Crown of Skemfar from the graveyard to its owner's hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Crown of Skemfar")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val crown = game.state.getZone(ZoneKey(game.player1Id, Zone.GRAVEYARD)).single()
                val abilityId = cardRegistry.getCard("Crown of Skemfar")!!.activatedAbilities.single().id

                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = crown, abilityId = abilityId),
                ).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("Crown leaves the graveyard") {
                    game.state.getZone(ZoneKey(game.player1Id, Zone.GRAVEYARD)) shouldBe emptyList()
                }
                withClue("Crown returns to hand") {
                    game.findCardsInHand(1, "Crown of Skemfar").single() shouldBe crown
                }
            }
        }
    }
}
