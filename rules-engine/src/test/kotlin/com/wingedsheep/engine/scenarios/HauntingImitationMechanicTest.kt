package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsRevealedEvent
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Mechanic coverage for [Patterns.Library.eachPlayerRevealTop],
 * [Patterns.Mechanic.eachPlayerRevealTopCreaturesCreateCopiesElseReturnSource], and
 * [com.wingedsheep.sdk.dsl.Effects.ReturnSourceSpellToOwnersHand].
 *
 * Haunting Imitation is the motivating card but is not authored here — these tests pin the shared
 * pipeline: symmetric top-of-library reveal, Spirit token copies with overrides, and the empty-
 * creature return-to-hand rider.
 */
class HauntingImitationMechanicTest : FunSpec({

    val projector = StateProjector()

    val HauntingImitationProbe = card("Haunting Imitation Probe") {
        manaCost = "{2}{U}"
        typeLine = "Sorcery"
        oracleText = "Each player reveals the top card of their library. For each creature card " +
            "revealed this way, create a token that's a copy of that card, except it's 1/1, it's " +
            "a Spirit in addition to its other types, and it has flying. If no creature cards were " +
            "revealed this way, return Haunting Imitation to its owner's hand."
        spell {
            effect = Patterns.Mechanic.eachPlayerRevealTopCreaturesCreateCopiesElseReturnSource()
        }
    }

    fun driver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + HauntingImitationProbe)
        return driver
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 40 && driver.state.stack.isNotEmpty() && !driver.isPaused) {
            driver.bothPass()
        }
    }

    fun castProbe(driver: GameTestDriver, caster: EntityId): EntityId {
        val spell = driver.putCardInHand(caster, "Haunting Imitation Probe")
        driver.giveMana(caster, Color.BLUE, 3)
        driver.castSpell(caster, spell).isSuccess shouldBe true
        resolveStack(driver)
        return spell
    }

    fun tokenCopies(driver: GameTestDriver, controller: EntityId, copiedName: String): List<EntityId> =
        driver.state.getZone(controller, Zone.BATTLEFIELD).filter { id ->
            val entity = driver.state.getEntity(id) ?: return@filter false
            entity.get<TokenComponent>() != null &&
                entity.get<CardComponent>()?.name == copiedName
        }

    test("each player reveals the top card of their library publicly") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40, "Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCardOnTopOfLibrary(active, "Grizzly Bears")
        driver.putCardOnTopOfLibrary(opponent, "Island")

        castProbe(driver, active)

        val reveals = driver.events.filterIsInstance<CardsRevealedEvent>()
        reveals.any { it.cardNames.contains("Grizzly Bears") && it.cardNames.contains("Island") } shouldBe true
    }

    test("a revealed creature creates a 1/1 Spirit token copy with flying for the caster") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40, "Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCardOnTopOfLibrary(active, "Grizzly Bears")
        driver.putCardOnTopOfLibrary(opponent, "Island")

        val spell = castProbe(driver, active)

        val tokens = tokenCopies(driver, active, "Grizzly Bears")
        tokens.size shouldBe 1

        val projected = projector.project(driver.state)
        val tokenId = tokens.single()
        projected.getPower(tokenId) shouldBe 1
        projected.getToughness(tokenId) shouldBe 1
        projected.hasKeyword(tokenId, Keyword.FLYING) shouldBe true
        projected.hasSubtype(tokenId, "Spirit") shouldBe true
        projected.hasType(tokenId, "CREATURE") shouldBe true

        driver.state.getZone(ZoneKey(active, Zone.GRAVEYARD)) shouldContain spell
        driver.state.getZone(ZoneKey(active, Zone.HAND)) shouldNotContain spell
    }

    test("when every revealed card is a noncreature the spell returns to its owner's hand") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40, "Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCardOnTopOfLibrary(active, "Island")
        driver.putCardOnTopOfLibrary(opponent, "Forest")

        val spell = castProbe(driver, active)

        tokenCopies(driver, active, "Grizzly Bears").size shouldBe 0
        driver.state.getZone(ZoneKey(active, Zone.HAND)) shouldContain spell
        driver.state.getZone(ZoneKey(active, Zone.GRAVEYARD)) shouldNotContain spell
    }

    test("a single revealed creature among noncreatures creates one token and keeps the spell in the graveyard") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40, "Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCardOnTopOfLibrary(active, "Grizzly Bears")
        driver.putCardOnTopOfLibrary(opponent, "Island")

        val spell = castProbe(driver, active)

        tokenCopies(driver, active, "Grizzly Bears").size shouldBe 1
        driver.state.getZone(ZoneKey(active, Zone.GRAVEYARD)) shouldContain spell
        driver.state.getZone(ZoneKey(active, Zone.HAND)) shouldNotContain spell
    }

    test("empty libraries reveal nothing and return the spell to hand") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40, "Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        while (driver.state.getZone(ZoneKey(active, Zone.LIBRARY)).isNotEmpty()) {
            val top = driver.state.getZone(ZoneKey(active, Zone.LIBRARY)).first()
            driver.moveToGraveyard(top)
        }
        while (driver.state.getZone(ZoneKey(opponent, Zone.LIBRARY)).isNotEmpty()) {
            val top = driver.state.getZone(ZoneKey(opponent, Zone.LIBRARY)).first()
            driver.moveToGraveyard(top)
        }

        val spell = castProbe(driver, active)

        tokenCopies(driver, active, "Grizzly Bears").size shouldBe 0
        driver.state.getZone(ZoneKey(active, Zone.HAND)) shouldContain spell
        driver.state.getZone(ZoneKey(active, Zone.GRAVEYARD)) shouldNotContain spell
    }

    test("three-player game creates one token per revealed creature across all players") {
        val driver = driver()
        val players = driver.initMultiplayer(
            decks = List(3) { Deck.of("Island" to 40, "Forest" to 40) },
            startingLife = 20,
            skipMulligans = true,
        )
        val active = players[0]
        val second = players[1]
        val third = players[2]

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCardOnTopOfLibrary(active, "Grizzly Bears")
        driver.putCardOnTopOfLibrary(second, "Hill Giant")
        driver.putCardOnTopOfLibrary(third, "Island")

        castProbe(driver, active)

        tokenCopies(driver, active, "Grizzly Bears").size shouldBe 1
        tokenCopies(driver, active, "Hill Giant").size shouldBe 1
        tokenCopies(driver, active, "Island").size shouldBe 0
    }
})
