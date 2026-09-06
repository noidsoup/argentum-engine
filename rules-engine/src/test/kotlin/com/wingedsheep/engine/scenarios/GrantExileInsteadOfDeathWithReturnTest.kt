package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Mechanic tests for [Effects.GrantExileInsteadOfDeathFromBattlefieldWithReturn] — the turn-long
 * "if your permanent would die from the battlefield, exile it instead and return it at the next
 * end step" grant (Cosmic Intervention). Pins the granted [RedirectZoneChangeWithEffect] read path
 * and the replacement rider that schedules a delayed return.
 */
class GrantExileInsteadOfDeathWithReturnTest : FunSpec({

    val CosmicShield = card("Cosmic Shield Test") {
        manaCost = "{2}{W}"
        typeLine = "Sorcery"
        oracleText = "If a permanent you control would be put into a graveyard from the battlefield " +
            "this turn, exile it instead. Return it to the battlefield under its owner's control " +
            "at the beginning of the next end step."
        spell {
            effect = Effects.GrantExileInsteadOfDeathFromBattlefieldWithReturn()
        }
    }

    val DiesWatcher = card("Dies Watcher Test") {
        manaCost = "{1}{B}"
        typeLine = "Creature — Human"
        power = 1
        toughness = 1
        oracleText = "Whenever a creature you control dies, draw a card."
        triggeredAbility {
            trigger = TriggerSpec(
                event = EventPattern.ZoneChangeEvent(
                    filter = GameObjectFilter.Creature.youControl(),
                    from = Zone.BATTLEFIELD,
                    to = Zone.GRAVEYARD
                )
            )
            effect = Effects.DrawCards(1)
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(CosmicShield, DiesWatcher))
        d.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Grizzly Bears" to 20, "Lightning Bolt" to 10),
            skipMulligans = true,
        )
        return d
    }

    fun castCosmicShield(d: GameTestDriver, player: com.wingedsheep.sdk.model.EntityId) {
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val shieldId = d.putCardInHand(player, CosmicShield.name)
        d.giveMana(player, Color.WHITE, 3)
        d.submitSuccess(CastSpell(player, shieldId))
        d.bothPass()
    }

    test("a permanent that would die is exiled instead of going to the graveyard") {
        val d = driver()
        val p1 = d.activePlayer!!
        castCosmicShield(d, p1)

        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        val boltId = d.putCardInHand(p1, "Lightning Bolt")
        d.giveMana(p1, Color.RED, 1)
        d.castSpell(p1, boltId, targets = listOf(bears))
        d.bothPass()

        d.getExile(p1).shouldContain(bears)
        d.getGraveyard(p1).shouldNotContain(bears)
        d.state.getBattlefield().shouldNotContain(bears)
    }

    test("the exiled permanent returns to the battlefield at the beginning of the next end step") {
        val d = driver()
        val p1 = d.activePlayer!!
        castCosmicShield(d, p1)

        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        val boltId = d.putCardInHand(p1, "Lightning Bolt")
        d.giveMana(p1, Color.RED, 1)
        d.castSpell(p1, boltId, targets = listOf(bears))
        d.bothPass()

        d.getExile(p1).shouldContain(bears)
        d.state.getBattlefield().shouldNotContain(bears)

        d.passPriorityUntil(Step.END)
        d.bothPass()

        d.state.getBattlefield().shouldContain(bears)
        d.getExile(p1).shouldNotContain(bears)
    }

    test("the redirect is a replacement — dies triggers do not fire") {
        val d = driver()
        val p1 = d.activePlayer!!
        val handBefore = d.getHand(p1).size

        castCosmicShield(d, p1)
        d.putCreatureOnBattlefield(p1, DiesWatcher.name)
        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")

        val boltId = d.putCardInHand(p1, "Lightning Bolt")
        d.giveMana(p1, Color.RED, 1)
        d.castSpell(p1, boltId, targets = listOf(bears))
        d.bothPass()

        d.getHand(p1).size.shouldBe(handBefore)
        d.getExile(p1).shouldContain(bears)
    }

    test("the granted replacement expires in the cleanup step") {
        val d = driver()
        val p1 = d.activePlayer!!
        castCosmicShield(d, p1)
        d.state.grantedReplacementEffects.isNotEmpty().shouldBeTrue()

        d.passPriorityUntil(Step.END)
        d.bothPass()
        d.bothPass()

        d.state.grantedReplacementEffects.isEmpty().shouldBeTrue()
    }

    test("each redirected permanent schedules its own delayed return") {
        val d = driver()
        val p1 = d.activePlayer!!
        castCosmicShield(d, p1)

        val bears1 = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        val bears2 = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        val bolt1 = d.putCardInHand(p1, "Lightning Bolt")
        d.giveMana(p1, Color.RED, 1)
        d.castSpell(p1, bolt1, targets = listOf(bears1))
        d.bothPass()

        val bolt2 = d.putCardInHand(p1, "Lightning Bolt")
        d.giveMana(p1, Color.RED, 1)
        d.castSpell(p1, bolt2, targets = listOf(bears2))
        d.bothPass()

        d.getExile(p1).shouldContain(bears1)
        d.getExile(p1).shouldContain(bears2)

        d.passPriorityUntil(Step.CLEANUP)

        d.state.getBattlefield().filter { d.getCardName(it) == "Grizzly Bears" }.size shouldBe 2
    }

    test("a delayed return scheduled during the end step waits until the next end step") {
        val d = driver()
        val p1 = d.activePlayer!!
        castCosmicShield(d, p1)

        d.passPriorityUntil(Step.END)

        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        val boltId = d.putCardInHand(p1, "Lightning Bolt")
        d.giveMana(p1, Color.RED, 1)
        d.castSpell(p1, boltId, targets = listOf(bears))
        d.bothPass()

        d.getExile(p1).shouldContain(bears)
        d.state.getBattlefield().shouldNotContain(bears)

        // Still the same end step — return has not happened yet.
        d.bothPass()
        d.state.getBattlefield().shouldNotContain(bears)

        d.passPriorityUntil(Step.CLEANUP)
        d.passPriorityUntil(Step.UPKEEP)
        d.passPriorityUntil(Step.END)
        d.bothPass()

        d.state.getBattlefield().shouldContain(bears)
    }
})
