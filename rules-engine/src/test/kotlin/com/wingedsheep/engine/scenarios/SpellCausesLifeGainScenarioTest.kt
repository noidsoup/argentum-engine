package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.CycleCard
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.mtg.sets.definitions.ons.cards.RenewedFaith
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeywordToOwnSpells
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Feature F-LIFEGAIN-CAUSE: life gain caused by a spell can be filtered for triggers.
 *
 * Oracle shape under test: "Whenever a white instant or sorcery spell causes you to gain life…"
 * (Firesong and Sunspeaker). Fixture watcher deals 3 to each opponent when that fires — no
 * production Firesong card until this matrix is green.
 *
 * Rulings covered:
 *  - Direct gain-life instruction on a white I/S
 *  - White I/S with lifelink dealing damage
 *  - Spell instructs a lifelink permanent to deal damage → cause is the spell
 *  - Red I/S with granted lifelink does not match a white filter
 *  - Combat / permanent-ability life gain does not match
 *  - Opponent's white I/S causing you to gain life still matches
 */
class SpellCausesLifeGainScenarioTest : FunSpec({

    val whiteFilter = GameObjectFilter.InstantOrSorcery.withColor(Color.WHITE)

    val watcher = card("Spell Life Gain Watcher") {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 1
        toughness = 1
        oracleText = "Whenever a white instant or sorcery spell causes you to gain life, " +
            "this creature deals 3 damage to each opponent."
        triggeredAbility {
            trigger = Triggers.spellCausesYouToGainLife(whiteFilter)
            effect = Effects.DealDamage(3, EffectTarget.PlayerRef(Player.EachOpponent))
        }
    }

    val whiteGainThree = card("White Gain Three") {
        manaCost = "{W}"
        colorIdentity = "W"
        typeLine = "Instant"
        oracleText = "You gain 3 life."
        spell {
            effect = Effects.GainLife(3)
        }
    }

    val whiteBurnLifelink = card("White Burn Lifelink") {
        manaCost = "{W}"
        colorIdentity = "W"
        typeLine = "Instant"
        oracleText = "Lifelink\nThis spell deals 2 damage to each opponent."
        keywords(Keyword.LIFELINK)
        spell {
            effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
        }
    }

    val whiteInstructCreature = card("White Instruct Creature") {
        manaCost = "{W}"
        colorIdentity = "W"
        typeLine = "Sorcery"
        oracleText = "Target creature you control deals 2 damage to each opponent."
        spell {
            val creature = target("target creature you control", Targets.CreatureYouControl)
            effect = Effects.DealDamage(
                2,
                EffectTarget.PlayerRef(Player.EachOpponent),
                damageSource = creature
            )
        }
    }

    val redBurn = card("Red Burn Two") {
        manaCost = "{R}"
        colorIdentity = "R"
        typeLine = "Instant"
        oracleText = "This spell deals 2 damage to each opponent."
        spell {
            effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent))
        }
    }

    val redSpellLifelinkLord = card("Red Spell Lifelink Lord") {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 1
        toughness = 1
        oracleText = "Red instant and sorcery spells you control have lifelink."
        staticAbility {
            ability = GrantKeywordToOwnSpells(
                keyword = Keyword.LIFELINK,
                spellFilter = GameObjectFilter.InstantOrSorcery.withColor(Color.RED),
            )
        }
    }

    val lifelinkBearer = card("Lifelink Bearer") {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 2
        toughness = 2
        oracleText = "Lifelink"
        keywords(Keyword.LIFELINK)
    }

    val etbGainLife = card("ETB Gain Life") {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 1
        toughness = 1
        oracleText = "When this creature enters, you gain 3 life."
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.GainLife(3)
        }
    }

    val whiteGiftLife = card("White Gift Life") {
        manaCost = "{W}"
        colorIdentity = "W"
        typeLine = "Instant"
        oracleText = "Target player gains 3 life."
        spell {
            val player = target("target player", Targets.Player)
            effect = Effects.GainLife(3, player)
        }
    }

    val whiteDividedLifelink = card("White Divided Lifelink") {
        manaCost = "{W}"
        colorIdentity = "W"
        typeLine = "Instant"
        oracleText = "Lifelink\nThis spell deals 2 damage divided as you choose among one or two targets."
        keywords(Keyword.LIFELINK)
        spell {
            target = com.wingedsheep.sdk.scripting.targets.AnyTarget(count = 2, minCount = 1)
            // Composite so cast-time distribution is not required (legacy resolve-time pause path).
            effect = Effects.Composite(
                com.wingedsheep.sdk.scripting.effects.DividedDamageEffect(
                    totalDamage = 2,
                    minTargets = 1,
                    maxTargets = 2,
                )
            )
        }
    }

    // Scry pauses the resolution for a select-cards decision, so the life gain lands on resume —
    // by which time a copy of this spell has already ceased to exist (CR 707.10a).
    val whiteScryThenGain = card("White Scry Then Gain") {
        manaCost = "{W}"
        colorIdentity = "W"
        typeLine = "Instant"
        oracleText = "Scry 1. You gain 3 life."
        spell {
            effect = Effects.Composite(
                Effects.Scry(1),
                Effects.GainLife(3),
            )
        }
    }

    // No printed lifelink — only Red Spell Lifelink Lord grants it (Firesong-shaped).
    val redDividedBurn = card("Red Divided Burn") {
        manaCost = "{R}"
        colorIdentity = "R"
        typeLine = "Instant"
        oracleText = "This spell deals 2 damage divided as you choose among one or two targets."
        spell {
            target = com.wingedsheep.sdk.scripting.targets.AnyTarget(count = 2, minCount = 1)
            effect = Effects.Composite(
                com.wingedsheep.sdk.scripting.effects.DividedDamageEffect(
                    totalDamage = 2,
                    minTargets = 1,
                    maxTargets = 2,
                )
            )
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(
            TestCards.all + listOf(
                watcher,
                whiteGainThree,
                whiteBurnLifelink,
                whiteInstructCreature,
                redBurn,
                redSpellLifelinkLord,
                lifelinkBearer,
                etbGainLife,
                whiteGiftLife,
                whiteDividedLifelink,
                redDividedBurn,
                whiteScryThenGain,
                RenewedFaith,
            )
        )
        return d
    }

    fun resolveUntilIdle(d: GameTestDriver, maxPasses: Int = 40) {
        var guard = 0
        while ((d.pendingDecision != null || d.stackSize > 0) && guard++ < maxPasses) {
            if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass()
        }
    }

    test("white instant that says gain life causes the filtered trigger") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Spell Life Gain Watcher")
        val spell = d.putCardInHand(me, "White Gain Three")
        d.giveMana(me, Color.WHITE, 1)

        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        d.castSpell(me, spell).isSuccess shouldBe true
        resolveUntilIdle(d)

        d.getLifeTotal(me) shouldBe myLife + 3
        d.getLifeTotal(opp) shouldBe oppLife - 3
    }

    test("white instant with lifelink dealing damage causes the filtered trigger") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Spell Life Gain Watcher")
        val spell = d.putCardInHand(me, "White Burn Lifelink")
        d.giveMana(me, Color.WHITE, 1)

        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        d.castSpell(me, spell).isSuccess shouldBe true
        resolveUntilIdle(d)

        // 2 damage to opp + lifelink +3 from watcher
        d.getLifeTotal(me) shouldBe myLife + 2
        d.getLifeTotal(opp) shouldBe oppLife - 2 - 3
    }

    test("white spell instructing a lifelink creature to deal damage attributes cause to the spell") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Spell Life Gain Watcher")
        val bearer = d.putCreatureOnBattlefield(me, "Lifelink Bearer")
        val spell = d.putCardInHand(me, "White Instruct Creature")
        d.giveMana(me, Color.WHITE, 1)

        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        d.castSpell(me, spell, listOf(bearer)).isSuccess shouldBe true
        resolveUntilIdle(d)

        d.getLifeTotal(me) shouldBe myLife + 2
        d.getLifeTotal(opp) shouldBe oppLife - 2 - 3
    }

    test("red instant with granted lifelink does not match a white causing-source filter") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Spell Life Gain Watcher")
        d.putCreatureOnBattlefield(me, "Red Spell Lifelink Lord")
        val spell = d.putCardInHand(me, "Red Burn Two")
        d.giveMana(me, Color.RED, 1)

        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        d.castSpell(me, spell).isSuccess shouldBe true
        resolveUntilIdle(d)

        // Lifelink from the red grant works; watcher must NOT fire.
        d.getLifeTotal(me) shouldBe myLife + 2
        d.getLifeTotal(opp) shouldBe oppLife - 2
    }

    test("combat lifelink does not match spell-caused life-gain filter") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)

        d.putCreatureOnBattlefield(me, "Spell Life Gain Watcher")
        val bearer = d.putCreatureOnBattlefield(me, "Lifelink Bearer")
        d.removeSummoningSickness(bearer)

        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(bearer), opp).error shouldBe null
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareNoBlockers(opp)
        d.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        d.getLifeTotal(me) shouldBe myLife + 2
        d.getLifeTotal(opp) shouldBe oppLife - 2
    }

    test("permanent ETB life gain does not match spell-caused filter") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Spell Life Gain Watcher")
        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        // Direct placement does not fire ETB; cast as a {0} creature so the ETB trigger runs.
        val etb = d.putCardInHand(me, "ETB Gain Life")
        d.castSpell(me, etb).isSuccess shouldBe true
        resolveUntilIdle(d)

        d.getLifeTotal(me) shouldBe myLife + 3
        d.getLifeTotal(opp) shouldBe oppLife
    }

    test("opponent's white spell causing you to gain life still matches") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Spell Life Gain Watcher")

        // Opponent casts on my turn (instant) targeting me — Firesong ruling: you don't
        // have to control the white spell.
        d.passPriority(me)
        val spell = d.putCardInHand(opp, "White Gift Life")
        d.giveMana(opp, Color.WHITE, 1)

        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        d.castSpell(opp, spell, listOf(me)).isSuccess shouldBe true
        resolveUntilIdle(d)

        d.getLifeTotal(me) shouldBe myLife + 3
        d.getLifeTotal(opp) shouldBe oppLife - 3
    }

    test("cycling a white instant that gains life does not match spell-caused filter") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Spell Life Gain Watcher")
        val faith = d.putCardInHand(me, "Renewed Faith")
        d.giveMana(me, Color.WHITE, 1)
        d.giveColorlessMana(me, 1)

        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        d.submit(CycleCard(playerId = me, cardId = faith)).isSuccess shouldBe true
        var guard = 0
        while ((d.pendingDecision != null || d.stackSize > 0) && guard++ < 40) {
            when (val decision = d.pendingDecision) {
                is com.wingedsheep.engine.core.YesNoDecision ->
                    d.submitYesNo(decision.playerId, true)
                null -> d.bothPass()
                else -> d.autoResolveDecision()
            }
        }

        // Cycle trigger may-gain-2 fires; Firesong watcher must not (ability, not a spell).
        d.getLifeTotal(me) shouldBe myLife + 2
        d.getLifeTotal(opp) shouldBe oppLife
    }

    test("copy of a white lifelink instant still matches after the copy ceases to exist") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Spell Life Gain Watcher")
        d.replaceState(
            d.state.copy(
                pendingSpellCopies = listOf(
                    com.wingedsheep.engine.state.PendingSpellCopy(
                        controllerId = me,
                        copies = 1,
                        sourceId = me,
                        sourceName = "Test Copier",
                        spellFilter = GameObjectFilter.InstantOrSorcery,
                    )
                )
            )
        )

        val spell = d.putCardInHand(me, "White Burn Lifelink")
        d.giveMana(me, Color.WHITE, 1)

        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        d.castSpell(me, spell).isSuccess shouldBe true
        resolveUntilIdle(d)

        // Original + copy each deal 2 with lifelink (+4), and watcher fires twice (+6 to opp).
        d.getLifeTotal(me) shouldBe myLife + 4
        d.getLifeTotal(opp) shouldBe oppLife - 4 - 6
    }

    test("copy that pauses mid-resolution still matches once the copy is gone") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Spell Life Gain Watcher")
        d.replaceState(
            d.state.copy(
                pendingSpellCopies = listOf(
                    com.wingedsheep.engine.state.PendingSpellCopy(
                        controllerId = me,
                        copies = 1,
                        sourceId = me,
                        sourceName = "Test Copier",
                        spellFilter = GameObjectFilter.InstantOrSorcery,
                    )
                )
            )
        )

        val spell = d.putCardInHand(me, "White Scry Then Gain")
        d.giveMana(me, Color.WHITE, 1)

        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        d.castSpell(me, spell).isSuccess shouldBe true
        resolveUntilIdle(d)

        // The scry pause retires the copy before its GainLife runs; the stamped LKI is the only
        // thing left that can tell the watcher's filter this was a white instant.
        d.getLifeTotal(me) shouldBe myLife + 6
        d.getLifeTotal(opp) shouldBe oppLife - 6
    }

    test("white divided-damage lifelink with cast-time distribution still matches") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Spell Life Gain Watcher")
        val c1 = d.putCreatureOnBattlefield(opp, "Grizzly Bears")
        val c2 = d.putCreatureOnBattlefield(opp, "Grizzly Bears")

        val spell = d.putCardInHand(me, "White Divided Lifelink")
        d.giveMana(me, Color.WHITE, 1)

        val myLife = d.getLifeTotal(me)
        val oppLife = d.getLifeTotal(opp)

        d.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(c1), ChosenTarget.Permanent(c2)),
                damageDistribution = mapOf(c1 to 1, c2 to 1),
                paymentStrategy = PaymentStrategy.FromPool,
            ),
        ).isSuccess shouldBe true
        resolveUntilIdle(d)

        // 1+1 damage → two lifelink gains of 1 (sequential packets) → watcher fires twice.
        d.getLifeTotal(me) shouldBe myLife + 2
        d.getLifeTotal(opp) shouldBe oppLife - 6
    }

    test("granted lifelink on red divided damage still applies with cast-time distribution") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val me = d.activePlayer!!
        val opp = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Red Spell Lifelink Lord")
        val c1 = d.putCreatureOnBattlefield(opp, "Grizzly Bears")
        val c2 = d.putCreatureOnBattlefield(opp, "Grizzly Bears")

        val spell = d.putCardInHand(me, "Red Divided Burn")
        d.giveMana(me, Color.RED, 1)

        val myLife = d.getLifeTotal(me)

        d.submit(
            CastSpell(
                playerId = me,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(c1), ChosenTarget.Permanent(c2)),
                damageDistribution = mapOf(c1 to 1, c2 to 1),
                paymentStrategy = PaymentStrategy.FromPool,
            ),
        ).isSuccess shouldBe true
        resolveUntilIdle(d)

        d.getLifeTotal(me) shouldBe myLife + 2
    }
})
