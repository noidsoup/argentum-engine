package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.COUNCIL_WINNERS
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * Mechanic tests for Will of the council voting (CR 701.38) via [Effects.Vote],
 * [Patterns.Hand.playerChoiceFromGraveyard], and [Patterns.Mechanic.council].
 *
 * Custodi Squire is the motivating card but is not authored here — these tests pin the shared
 * primitive the card will compose.
 */
class WillOfTheCouncilMechanicTest : FunSpec({

    val VoteArtifact = card("Council Vote Artifact") {
        manaCost = "{2}"
        typeLine = "Artifact"
        oracleText = "Test artifact for council voting."
    }

    val VoteCreature = card("Council Vote Creature") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Elf"
        power = 1
        toughness = 1
        oracleText = "Test creature for council voting."
    }

    val VoteEnchantment = card("Council Vote Enchantment") {
        manaCost = "{W}"
        typeLine = "Enchantment"
        oracleText = "Test enchantment for council voting."
    }

    val CouncilProbe = card("Council Probe") {
        manaCost = "{1}{U}"
        typeLine = "Sorcery"
        oracleText = "Will of the council — vote on graveyard cards, return the winner(s) to hand."
        spell {
            effect = Patterns.Mechanic.council(
                vote = Patterns.Hand.playerChoiceFromGraveyard(
                    owner = Player.You,
                    filter = GameObjectFilter.ArtifactCreatureOrEnchantment,
                ),
                payoff = MoveCollectionEffect(
                    from = COUNCIL_WINNERS,
                    destination = CardDestination.ToZone(Zone.HAND, Player.You),
                ),
            )
        }
    }

    val allCards = TestCards.all + listOf(
        VoteArtifact, VoteCreature, VoteEnchantment, CouncilProbe,
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(allCards)
        return driver
    }

    fun GameTestDriver.putInGraveyard(player: EntityId, name: String): EntityId {
        val id = putCardInHand(player, name)
        moveToGraveyard(id)
        return id
    }

    fun GameTestDriver.castCouncilProbe(player: EntityId) {
        val spell = putCardInHand(player, "Council Probe")
        giveMana(player, Color.BLUE, 2)
        castSpell(player, spell).isSuccess shouldBe true
        bothPass()
    }

    /** Cast each vote in order, choosing [choices] per prompt. Returns who was asked, in order. */
    fun GameTestDriver.answerVotes(choices: List<EntityId>): List<EntityId> {
        val asked = mutableListOf<EntityId>()
        choices.forEach { choice ->
            val decision = pendingDecision as? SelectCardsDecision
                ?: error("Expected SelectCardsDecision, got $pendingDecision")
            asked += decision.playerId
            submitDecision(decision.playerId, CardsSelectedResponse(decision.id, listOf(choice)))
        }
        return asked
    }

    test("the card with the most votes returns to the controller's hand") {
        val driver = createDriver()
        driver.initMirrorMatch(Deck.of("Island" to 40, "Forest" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val artifact = driver.putInGraveyard(active, "Council Vote Artifact")
        val creature = driver.putInGraveyard(active, "Council Vote Creature")
        driver.putInGraveyard(active, "Council Vote Enchantment")

        driver.castCouncilProbe(active)
        // Controller votes first (starting with you), then opponent.
        driver.answerVotes(listOf(creature, creature))

        driver.getHand(active).shouldContainExactlyInAnyOrder(creature)
        driver.getGraveyard(active) shouldContain artifact
    }

    test("options tied for most votes all return to hand") {
        val driver = createDriver()
        driver.initMirrorMatch(Deck.of("Island" to 40, "Forest" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val artifact = driver.putInGraveyard(active, "Council Vote Artifact")
        val creature = driver.putInGraveyard(active, "Council Vote Creature")

        driver.castCouncilProbe(active)
        driver.answerVotes(listOf(artifact, creature))

        driver.getHand(active).shouldContainExactlyInAnyOrder(artifact, creature)
    }

    test("an empty eligible graveyard skips voting and does nothing") {
        val driver = createDriver()
        driver.initMirrorMatch(Deck.of("Island" to 40, "Forest" to 40))
        val active = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.castCouncilProbe(active)

        driver.pendingDecision shouldBe null
        driver.getHandSize(active) shouldBe 0
    }

    test("three-player pod asks controller first then proceeds in turn order") {
        val driver = createDriver()
        val players = driver.initMultiplayer(
            decks = List(3) { Deck.of("Island" to 40) },
            skipMulligans = true,
            startingPlayer = 1,
        )
        val controller = players[1]
        val next = players[2]
        val last = players[0]

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val winner = driver.putInGraveyard(controller, "Council Vote Artifact")
        val runnerUp = driver.putInGraveyard(controller, "Council Vote Creature")
        driver.putInGraveyard(controller, "Council Vote Enchantment")

        driver.castCouncilProbe(controller)

        val asked = driver.answerVotes(listOf(winner, runnerUp, winner))
        asked shouldBe listOf(controller, next, last)
        driver.getHand(controller) shouldBe listOf(winner)
    }

})
