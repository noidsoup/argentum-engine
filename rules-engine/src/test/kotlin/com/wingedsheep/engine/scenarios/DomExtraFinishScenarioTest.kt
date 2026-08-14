package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dom.cards.ChandraBoldPyromancer
import com.wingedsheep.mtg.sets.definitions.dom.cards.ChandrasOutburst
import com.wingedsheep.mtg.sets.definitions.dom.cards.FiresongAndSunspeaker
import com.wingedsheep.mtg.sets.definitions.dom.cards.NiambiFaithfulHealer
import com.wingedsheep.mtg.sets.definitions.dom.cards.TeferiTimebender
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.references.Player
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * DOM Extra finish — the five remaining promo/extra cards after the proof batch.
 *
 * Covers tutors, both planeswalkers' loyalty abilities, and Firesong's spell-caused life-gain
 * trigger (depends on F-LIFEGAIN-CAUSE).
 */
class DomExtraFinishScenarioTest : FunSpec({

    val whiteGainThree = card("White Gain Three") {
        manaCost = "{W}"
        colorIdentity = "W"
        typeLine = "Instant"
        oracleText = "You gain 3 life."
        spell {
            effect = Effects.GainLife(3)
        }
        metadata {
            rarity = Rarity.COMMON
            collectorNumber = "9001"
            artist = "Test"
        }
    }

    val redBurnTwo = card("Red Burn Two") {
        manaCost = "{R}"
        colorIdentity = "R"
        typeLine = "Instant"
        oracleText = "This spell deals 2 damage to each opponent."
        spell {
            effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
        }
        metadata {
            rarity = Rarity.COMMON
            collectorNumber = "9002"
            artist = "Test"
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(
            TestCards.all + listOf(
                ChandrasOutburst,
                ChandraBoldPyromancer,
                FiresongAndSunspeaker,
                NiambiFaithfulHealer,
                TeferiTimebender,
                whiteGainThree,
                redBurnTwo,
            )
        )
        return d
    }

    fun resolveUntilIdle(d: GameTestDriver, maxPasses: Int = 50) {
        var guard = 0
        while ((d.pendingDecision != null || d.stackSize > 0) && guard++ < maxPasses) {
            if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass()
        }
    }

    fun waitForDecision(d: GameTestDriver, maxPasses: Int = 30) {
        var guard = 0
        while (d.pendingDecision == null && guard++ < maxPasses) d.bothPass()
    }

    fun loyalty(d: GameTestDriver, id: EntityId): Int =
        d.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.LOYALTY) ?: 0

    fun seedLoyalty(d: GameTestDriver, id: EntityId, amount: Int) {
        d.addComponent(id, CountersComponent(mapOf(CounterType.LOYALTY to amount)))
    }

    test("Chandra's Outburst deals 4 and tutors Chandra, Bold Pyromancer from library") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val chandraId = d.putCardOnTopOfLibrary(me, "Chandra, Bold Pyromancer")
        val spell = d.putCardInHand(me, "Chandra's Outburst")
        d.giveMana(me, Color.RED, 2)
        d.giveColorlessMana(me, 3)

        val oppLife = d.getLifeTotal(opp)
        d.castSpell(me, spell, listOf(opp)).isSuccess shouldBe true

        waitForDecision(d)
        d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        d.submitCardSelection(me, listOf(chandraId))
        resolveUntilIdle(d)

        d.getLifeTotal(opp) shouldBe oppLife - 4
        d.getHand(me).mapNotNull { d.getCardName(it) }.any { it == "Chandra, Bold Pyromancer" } shouldBe true
    }

    test("Chandra, Bold Pyromancer +1 adds RR and deals 2 to target player") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val chandra = d.putPermanentOnBattlefield(me, "Chandra, Bold Pyromancer")
        seedLoyalty(d, chandra, 5)
        val plusOne = ChandraBoldPyromancer.script.activatedAbilities[0]

        val oppLife = d.getLifeTotal(opp)
        d.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = chandra,
                abilityId = plusOne.id,
                targets = listOf(ChosenTarget.Player(opp)),
            )
        )
        resolveUntilIdle(d)

        d.getLifeTotal(opp) shouldBe oppLife - 2
        loyalty(d, chandra) shouldBe 6
        // RR added to pool
        val pool = d.state.getEntity(me)
            ?.get<com.wingedsheep.engine.state.components.player.ManaPoolComponent>()
        (pool?.red ?: 0) shouldBe 2
    }

    test("Chandra, Bold Pyromancer -7 damages player and their creatures/planeswalkers") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val chandra = d.putPermanentOnBattlefield(me, "Chandra, Bold Pyromancer")
        seedLoyalty(d, chandra, 7)
        val bears = d.putCreatureOnBattlefield(opp, "Grizzly Bears")
        val ultimate = ChandraBoldPyromancer.script.activatedAbilities[2]

        val oppLife = d.getLifeTotal(opp)
        d.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = chandra,
                abilityId = ultimate.id,
                targets = listOf(ChosenTarget.Player(opp)),
            )
        )
        resolveUntilIdle(d)

        d.getLifeTotal(opp) shouldBe oppLife - 10
        (bears in d.state.getBattlefield()) shouldBe false
        loyalty(d, chandra) shouldBe 0
    }

    test("Niambi may search library/graveyard for Teferi, Timebender") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 20, "Island" to 20), skipMulligans = true)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val teferiId = d.putCardOnTopOfLibrary(me, "Teferi, Timebender")
        val niambi = d.putCardInHand(me, "Niambi, Faithful Healer")
        d.giveMana(me, Color.WHITE, 1)
        d.giveMana(me, Color.BLUE, 1)
        d.giveColorlessMana(me, 1)

        d.castSpell(me, niambi).isSuccess shouldBe true

        var selected = false
        var guard = 0
        while (!selected && guard++ < 50) {
            when (val decision = d.pendingDecision) {
                is YesNoDecision -> d.submitYesNo(decision.playerId, true)
                is SelectCardsDecision -> {
                    d.submitCardSelection(me, listOf(teferiId))
                    selected = true
                }
                null -> d.bothPass()
                else -> d.autoResolveDecision()
            }
        }
        selected shouldBe true
        resolveUntilIdle(d)

        d.getHand(me).mapNotNull { d.getCardName(it) }.any { it == "Teferi, Timebender" } shouldBe true
    }

    test("Teferi, Timebender +2 untaps up to one artifact or creature") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val teferi = d.putPermanentOnBattlefield(me, "Teferi, Timebender")
        seedLoyalty(d, teferi, 5)
        val bears = d.putCreatureOnBattlefield(me, "Grizzly Bears")
        d.addComponent(bears, TappedComponent)

        val plusTwo = TeferiTimebender.script.activatedAbilities[0]
        d.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = teferi,
                abilityId = plusTwo.id,
                targets = listOf(ChosenTarget.Permanent(bears)),
            )
        )
        resolveUntilIdle(d)

        d.state.getEntity(bears)?.has<TappedComponent>() shouldBe false
        loyalty(d, teferi) shouldBe 7
    }

    test("Teferi, Timebender -3 gains 2 life and draws two cards") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val teferi = d.putPermanentOnBattlefield(me, "Teferi, Timebender")
        seedLoyalty(d, teferi, 5)
        d.putCardOnTopOfLibrary(me, "Mountain")
        d.putCardOnTopOfLibrary(me, "Mountain")

        val minusThree = TeferiTimebender.script.activatedAbilities[1]
        val life = d.getLifeTotal(me)
        val hand = d.getHand(me).size

        d.submitSuccess(
            ActivateAbility(playerId = me, sourceId = teferi, abilityId = minusThree.id)
        )
        resolveUntilIdle(d)

        d.getLifeTotal(me) shouldBe life + 2
        d.getHand(me).size shouldBe hand + 2
        loyalty(d, teferi) shouldBe 2
    }

    test("Firesong: white spell gaining life triggers 3 damage; red burn gains lifelink") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Firesong and Sunspeaker")

        val white = d.putCardInHand(me, "White Gain Three")
        d.giveMana(me, Color.WHITE, 1)
        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        d.castSpell(me, white).isSuccess shouldBe true

        var targeted = false
        var guard = 0
        while (!targeted && guard++ < 50) {
            when (val decision = d.pendingDecision) {
                is ChooseTargetsDecision -> {
                    d.submitTargetSelection(me, listOf(opp))
                    targeted = true
                }
                null -> d.bothPass()
                else -> d.autoResolveDecision()
            }
        }
        targeted shouldBe true
        resolveUntilIdle(d)

        d.getLifeTotal(me) shouldBe myLife + 3
        d.getLifeTotal(opp) shouldBe oppLife - 3

        val red = d.putCardInHand(me, "Red Burn Two")
        d.giveMana(me, Color.RED, 1)
        val myLife2 = d.getLifeTotal(me)
        val oppLife2 = d.getLifeTotal(opp)
        d.castSpell(me, red).isSuccess shouldBe true
        resolveUntilIdle(d)
        d.getLifeTotal(opp) shouldBe oppLife2 - 2
        d.getLifeTotal(me) shouldBe myLife2 + 2
    }
})
