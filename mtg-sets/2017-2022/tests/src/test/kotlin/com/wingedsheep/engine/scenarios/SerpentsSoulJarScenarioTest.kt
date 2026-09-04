package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Serpent's Soul-Jar (KHC #11).
 *
 * Whenever an Elf you control dies, exile it.
 */
class SerpentsSoulJarScenarioTest : ScenarioTestBase() {

    init {
        context("Serpent's Soul-Jar") {

            test("card definition is registered with dies and activated abilities") {
                val def = cardRegistry.requireCard("Serpent's Soul-Jar")
                def.triggeredAbilities.size shouldBe 1
                def.activatedAbilities.size shouldBe 1
            }


            test("dies trigger binds the dying Elf on the stack") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Serpent's Soul-Jar")
                    .withCardOnBattlefield(1, "Elvish Warrior")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(1, "Murder")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elf = game.findPermanent("Elvish Warrior")!!

                game.castSpell(1, "Murder", elf).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }

                var soulJarTrigger: TriggeredAbilityOnStackComponent? = null
                repeat(12) {
                    soulJarTrigger = game.state.stack.mapNotNull { id ->
                        game.state.getEntity(id)?.get<TriggeredAbilityOnStackComponent>()
                    }.firstOrNull { it.sourceName == "Serpent's Soul-Jar" }
                    if (soulJarTrigger != null) return@repeat
                    if (game.state.stack.isEmpty() && game.getPendingDecision() == null) return@repeat
                    game.passPriority()
                }

                withClue("Soul-Jar trigger is on the stack before it resolves") {
                    soulJarTrigger.shouldNotBeNull()
                    soulJarTrigger!!.triggeringEntityId shouldBe elf
                    game.state.getEntity(elf).shouldNotBeNull()
                    game.state.getZone(ZoneKey(game.player1Id, Zone.GRAVEYARD)) shouldContain elf
                }

                // Finish resolving the trigger.
                game.resolveStack()
            }

            test("when an Elf you control dies, it is exiled and linked to the Soul-Jar") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Serpent's Soul-Jar")
                    .withCardOnBattlefield(1, "Elvish Warrior")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInHand(1, "Murder")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val elf = game.findPermanent("Elvish Warrior")!!

                game.castSpell(1, "Murder", elf).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()


                val soulJar = game.findPermanent("Serpent's Soul-Jar")!!
                val exiledElf = game.state.getZone(ZoneKey(game.player1Id, Zone.EXILE))
                    .single { game.state.getEntity(it)?.get<CardComponent>()?.name == "Elvish Warrior" }

                withClue("dying Elf is exiled, not in graveyard") {
                    game.state.getZone(ZoneKey(game.player1Id, Zone.GRAVEYARD)) shouldBe emptyList()
                    game.state.getZone(ZoneKey(game.player1Id, Zone.EXILE)) shouldContain exiledElf
                }

                withClue("exiled Elf is linked to the Soul-Jar") {
                    val linked = game.state.getEntity(soulJar)?.get<com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent>()
                    linked?.exiledIds.orEmpty() shouldContain exiledElf
                }
            }
        }
    }
}
