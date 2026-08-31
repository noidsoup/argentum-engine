package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Sacred White Deer — Global Series: Jiang Yanggu & Mu Yanling #25
 * {1}{G} Creature — Elk, 2/2
 *
 * {3}{G}, {T}: You gain 4 life. Activate only if you control a Yanggu planeswalker.
 */
class SacredWhiteDeerScenarioTest : ScenarioTestBase() {

    private val abilityId =
        cardRegistry.getCard("Sacred White Deer")!!.script.activatedAbilities.first().id

    init {
        test("the life gain ability is not legal without a Yanggu planeswalker") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Sacred White Deer", summoningSickness = false)
                .withLandsOnBattlefield(1, "Forest", 4)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val deer = game.findPermanent("Sacred White Deer")!!

            val canActivate = game.getLegalActions(1).any { info ->
                val act = info.action
                act is ActivateAbility && act.sourceId == deer && act.abilityId == abilityId
            }

            withClue("without Jiang Yanggu the ability is not offered") {
                canActivate shouldBe false
            }
        }

        test("gains 4 life when activated with Jiang Yanggu on the battlefield") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withLifeTotal(1, 20)
                .withCardOnBattlefield(1, "Sacred White Deer", summoningSickness = false)
                .withCardOnBattlefield(1, "Jiang Yanggu")
                .withLandsOnBattlefield(1, "Forest", 4)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val deer = game.findPermanent("Sacred White Deer")!!

            game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = deer,
                    abilityId = abilityId,
                ),
            ).error shouldBe null
            game.resolveStack()

            withClue("the activated ability grants 4 life") {
                game.getLifeTotal(1) shouldBe 24
            }
        }
    }
}
