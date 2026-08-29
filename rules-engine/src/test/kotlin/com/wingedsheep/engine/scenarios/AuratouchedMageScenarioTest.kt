package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.AuratouchedMage
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.MoveToZoneEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

/**
 * Auratouched Mage (RAV / PC2) — search for an Aura that could enchant it, attach if still on the
 * battlefield, otherwise reveal to hand, then shuffle.
 */
class AuratouchedMageScenarioTest : FunSpec({

    val CreatureAura = card("Test Creature Buff Aura") {
        manaCost = "{W}"
        typeLine = "Enchantment — Aura"
        oracleText = "Enchant creature\nEnchanted creature gets +1/+1."
        auraTarget = Targets.Creature
        staticAbility { ability = ModifyStats(1, 1) }
    }

    val LandAura = card("Test Land Lock Aura") {
        manaCost = "{B}"
        typeLine = "Enchantment — Aura"
        oracleText = "Enchant land\nEnchanted land is a Swamp."
        auraTarget = Targets.Land
    }

    val DestroyCreature = card("Test Destroy Creature") {
        manaCost = "{1}{B}"
        typeLine = "Instant"
        spell {
            val t = target("target creature", TargetCreature())
            effect = MoveToZoneEffect(t, Zone.GRAVEYARD, byDestruction = true)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(AuratouchedMage, CreatureAura, LandAura, DestroyCreature))
        return driver
    }

    fun GameTestDriver.drainStackAndSelectSearch(player: EntityId, selected: List<EntityId> = emptyList()) {
        while (!isPaused && state.stack.isNotEmpty()) bothPass()
        if (isPaused && selected.isNotEmpty()) {
            submitCardSelection(player, selected)
            while (!isPaused && state.stack.isNotEmpty()) bothPass()
        }
    }

    test("searches library and attaches a legal Aura to itself") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val aura = driver.putCardOnTopOfLibrary(me, "Test Creature Buff Aura")
        driver.putCardOnTopOfLibrary(me, "Test Land Lock Aura")

        val mageHand = driver.putCardInHand(me, "Auratouched Mage")
        driver.giveMana(me, Color.WHITE, 1)
        driver.giveColorlessMana(me, 5)
        driver.castSpell(me, mageHand).isSuccess shouldBe true
        driver.drainStackAndSelectSearch(me, listOf(aura))

        val mage = driver.findPermanent(me, "Auratouched Mage")!!
        driver.findPermanent(me, "Test Creature Buff Aura") shouldBe aura
        driver.state.getEntity(aura)?.get<AttachedToComponent>()?.targetId shouldBe mage
        driver.getHand(me).contains(aura) shouldBe false
    }

    test("only offers Auras that could enchant the creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        driver.putCardOnTopOfLibrary(me, "Test Land Lock Aura")

        val mage = driver.putCardInHand(me, "Auratouched Mage")
        driver.giveMana(me, Color.WHITE, 1)
        driver.giveColorlessMana(me, 5)
        driver.castSpell(me, mage).isSuccess shouldBe true
        driver.drainStackAndSelectSearch(me)

        driver.isPaused shouldBe false
        driver.findPermanent(me, "Auratouched Mage") shouldBe mage
        driver.getHand(me).none { driver.getCardName(it) == "Test Land Lock Aura" } shouldBe true
    }

    test("reveals the Aura to hand when the mage left the battlefield before resolution") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Swamp" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!

        val aura = driver.putCardOnTopOfLibrary(me, "Test Creature Buff Aura")
        val mageHand = driver.putCardInHand(me, "Auratouched Mage")
        driver.giveMana(me, Color.WHITE, 1)
        driver.giveColorlessMana(me, 5)
        driver.castSpell(me, mageHand).isSuccess shouldBe true
        driver.bothPass() // Mage spell resolves; ETB trigger waits on the stack

        val mage = driver.findPermanent(me, "Auratouched Mage")!!
        driver.state.stack.isNotEmpty() shouldBe true

        val removal = driver.putCardInHand(me, "Test Destroy Creature")
        driver.giveMana(me, Color.BLACK, 2)
        driver.submitSuccess(
            com.wingedsheep.engine.core.CastSpell(
                playerId = me,
                cardId = removal,
                targets = listOf(ChosenTarget.Permanent(mage)),
            )
        )

        driver.drainStackAndSelectSearch(me, listOf(aura))

        driver.getGraveyard(me) shouldContain mage
        driver.getHand(me) shouldContain aura
        driver.state.getBattlefield().contains(aura) shouldBe false
        driver.state.getEntity(aura)?.get<AttachedToComponent>() shouldBe null
    }
})
