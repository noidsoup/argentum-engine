package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.CopyExceptions
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Spell-copy [CopyExceptions] on permanent-spell copies (CR 707.10f).
 *
 * Donal, Herald of Wings pattern: "copy it, except the copy is a 1/1 Spirit in addition to its
 * other types." Inline test card only — the VOC printing ships in a follow-up add-card cycle.
 */
class CopyTargetSpellExceptionsMechanicScenarioTest : FunSpec({

    val heraldPattern = card("Herald of Wings Pattern") {
        manaCost = "{U}"
        typeLine = "Instant"
        spell {
            val creatureSpell = target("creature spell you control", Targets.CreatureSpellYouControl)
            effect = Effects.CopyTargetSpell(
                target = creatureSpell,
                exceptions = CopyExceptions(
                    powerOverride = 1,
                    toughnessOverride = 1,
                    addedSubtypes = setOf(Subtype.SPIRIT),
                ),
            )
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(heraldPattern))
        return driver
    }

    test("a creature-spell copy with exceptions becomes a 1/1 Spirit token in addition to its other types") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Centaur Courser is a 3/3 Centaur Warrior creature spell.
        driver.giveMana(me, Color.GREEN, 3)
        val courser = driver.putCardInHand(me, "Centaur Courser")
        driver.submit(CastSpell(playerId = me, cardId = courser)).error shouldBe null

        // Copy the creature spell with the Donal-shaped exceptions.
        driver.giveMana(me, Color.BLUE, 1)
        val herald = driver.putCardInHand(me, "Herald of Wings Pattern")
        driver.submit(
            CastSpell(
                playerId = me,
                cardId = herald,
                targets = listOf(ChosenTarget.Spell(courser)),
            )
        ).error shouldBe null

        var guard = 0
        while (driver.state.stack.isNotEmpty() && guard++ < 30) driver.bothPass()

        val coursers = driver.getPermanents(me).filter {
            driver.state.getEntity(it)?.get<CardComponent>()?.name == "Centaur Courser"
        }
        withClue("the original and the token copy both resolve") {
            coursers.size shouldBe 2
        }

        val tokenCopy = coursers.first { driver.state.getEntity(it)?.has<TokenComponent>() == true }
        val realCourser = coursers.first { it != tokenCopy }
        val projected = driver.state.projectedState

        withClue("the real Centaur Courser keeps its printed stats") {
            projected.getPower(realCourser) shouldBe 3
            projected.getToughness(realCourser) shouldBe 3
        }
        withClue("except the copy is 1/1") {
            projected.getPower(tokenCopy) shouldBe 1
            projected.getToughness(tokenCopy) shouldBe 1
        }
        withClue("except the copy is a Spirit in addition to its other types") {
            projected.isCreature(tokenCopy) shouldBe true
            projected.hasSubtype(tokenCopy, Subtype.SPIRIT.value) shouldBe true
            projected.hasSubtype(tokenCopy, Subtype.CENTAUR.value) shouldBe true
            projected.hasSubtype(tokenCopy, Subtype.WARRIOR.value) shouldBe true
        }
        withClue("the token is a copy of the spell, not the original permanent") {
            tokenCopy shouldNotBe realCourser
        }
    }
})
