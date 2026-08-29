package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Enigma Sphinx (ARB #106 / PC2 #89) — when it leaves the battlefield to the graveyard, it tucks
 * itself third from the top of its owner's library.
 */
class EnigmaSphinxScenarioTest : ScenarioTestBase() {

    init {
        test("dying from the battlefield tucks Enigma Sphinx third from the top") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Enigma Sphinx")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Plains")
                .withCardInHand(1, "Murder")
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val sphinx = game.findPermanent("Enigma Sphinx")!!
            game.castSpell(1, "Murder", sphinx).error shouldBe null
            game.resolveStack()

            withClue("Enigma Sphinx leaves the graveyard for the library") {
                game.isInGraveyard(1, "Enigma Sphinx") shouldBe false
            }
            val library = game.state.getZone(ZoneKey(game.player1Id, Zone.LIBRARY))
            withClue("third from top (index 2) is Enigma Sphinx") {
                library[2] shouldBe sphinx
                game.state.getEntity(sphinx)?.get<CardComponent>()!!.name shouldBe "Enigma Sphinx"
            }
        }
    }
}
