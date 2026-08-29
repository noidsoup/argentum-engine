package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.handlers.effects.library.CastFromCollectionWithoutPayingCostExecutor
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gpt.cards.SkarrgTheRagePits
import com.wingedsheep.mtg.sets.definitions.pc2.cards.SilentBladeOni
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Skarrg, the Rage Pits (GPT #163) — a Gruul land with a mana ability and a
 * creature pump that grants trample until end of turn.
 */
class SkarrgTheRagePitsScenarioTest : FunSpec({

    val pumpAbilityId = SkarrgTheRagePits.activatedAbilities[1].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SkarrgTheRagePits))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Forest" to 20), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("pump ability gives +1/+1 and trample until end of turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!

        val skarrg = driver.putLandOnBattlefield(player, "Skarrg, the Rage Pits")
        val bears = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        driver.giveMana(player, Color.RED, 1)
        driver.giveMana(player, Color.GREEN, 1)

        driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = skarrg,
                abilityId = pumpAbilityId,
                targets = listOf(ChosenTarget.Permanent(bears)),
            ),
        ).isSuccess shouldBe true
        driver.bothPass()

        withClue("Grizzly Bears is 3/3 after the pump") {
            driver.state.projectedState.getPower(bears) shouldBe 3
            driver.state.projectedState.getToughness(bears) shouldBe 3
        }
        withClue("Grizzly Bears gains trample") {
            driver.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe true
        }
        withClue("Skarrg tapped to pay the cost") {
            driver.isTapped(skarrg) shouldBe true
        }
    }
})

/**
 * Scenario tests for Silent-Blade Oni (PC2 #105) — ninjutsu plus look-and-free-cast from the
 * damaged player's hand.
 */
class SilentBladeOniScenarioTest : FunSpec({

    test("may cast from an opponent's hand when a MayPlayPermission grants it") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bolt = driver.putCardInHand(driver.player2, "Lightning Bolt")
        val (_, granted) = CastFromCollectionWithoutPayingCostExecutor.grantFreeCast(
            state = driver.state,
            cardId = bolt,
            controllerId = driver.player1,
            sourceId = null,
        )
        driver.replaceState(granted)

        val cast = driver.submit(
            CastSpell(
                playerId = driver.player1,
                cardId = bolt,
                targets = listOf(ChosenTarget.Player(driver.player2)),
            ),
        )
        withClue("casting from opponent's hand with permission should succeed: ${cast.error}") {
            cast.error shouldBe null
        }
    }

    val freeSpell = card("Oni Test Free Spell") {
        manaCost = "{2}{U}"
        typeLine = "Enchantment"
        oracleText = "Test enchantment."
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SilentBladeOni))
        driver.registerCard(freeSpell)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun swingUnblocked(driver: GameTestDriver, attacker: com.wingedsheep.sdk.model.EntityId): com.wingedsheep.sdk.model.EntityId {
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(attacker), opponent)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareNoBlockers(opponent)
        var safety = 0
        while (!driver.isPaused && safety++ < 30) driver.bothPass()
        return opponent
    }

    test("combat damage lets the controller free-cast a spell from the damaged player's hand") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val oni = driver.putCreatureOnBattlefield(player, "Silent-Blade Oni")
        driver.removeSummoningSickness(oni)
        val spell = driver.putCardInHand(opponent, "Oni Test Free Spell")

        swingUnblocked(driver, oni)

        driver.isPaused shouldBe true
        driver.submitCardSelection(player, listOf(spell))
        driver.bothPass()
        driver.bothPass()

        withClue("the chosen spell left the opponent's hand") {
            (spell in driver.state.getZone(com.wingedsheep.engine.state.ZoneKey(opponent, com.wingedsheep.sdk.core.Zone.HAND))) shouldBe false
        }
        withClue("the free-cast permanent entered under the Oni player's control") {
            driver.findPermanent(player, "Oni Test Free Spell") shouldNotBe null
        }
    }

    test("combat damage can free-cast a targeted instant from the damaged player's hand") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val oni = driver.putCreatureOnBattlefield(player, "Silent-Blade Oni")
        driver.removeSummoningSickness(oni)
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")

        swingUnblocked(driver, oni)
        val lifeBeforeBolt = driver.getLifeTotal(opponent)

        driver.submitCardSelection(player, listOf(bolt))
        driver.bothPass()
        if (driver.pendingDecision is com.wingedsheep.engine.core.ChooseTargetsDecision) {
            driver.submitTargetSelection(player, listOf(opponent))
        }
        driver.bothPass()
        driver.bothPass()

        withClue("Lightning Bolt left the opponent's hand") {
            driver.findCardInHand(opponent, "Lightning Bolt") shouldBe null
        }
        withClue("Lightning Bolt dealt 3 damage to the opponent") {
            driver.getLifeTotal(opponent) shouldBe lifeBeforeBolt - 3
        }
    }

    test("declining the optional cast leaves the opponent's spell in hand") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val oni = driver.putCreatureOnBattlefield(player, "Silent-Blade Oni")
        driver.removeSummoningSickness(oni)
        driver.putCardInHand(opponent, "Lightning Bolt")

        swingUnblocked(driver, oni)

        driver.submitCardSelection(player, emptyList())
        repeat(4) { driver.bothPass() }

        withClue("Lightning Bolt stays in the opponent's hand") {
            driver.findCardInHand(opponent, "Lightning Bolt") shouldNotBe null
        }
    }
})

/**
 * Scenario tests for Throat Slitter (BOK #88) — ninjutsu plus destroy target nonblack creature
 * that the damaged player controls.
 */
class ThroatSlitterScenarioTest : ScenarioTestBase() {

    init {
        context("Throat Slitter") {

            fun advanceToDecision(game: TestGame) {
                var guard = 0
                while (!game.hasPendingDecision() && guard++ < 20) {
                    if (game.state.priorityPlayerId == null) break
                    game.passPriority()
                }
            }

            fun combatBoard() = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Throat Slitter")
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardOnBattlefield(2, "Black Creature") // black — not a legal target
                .withCardInLibrary(1, "Swamp")
                .withCardInLibrary(2, "Swamp")
                .withActivePlayer(1)
                .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

            fun swingUnblocked(game: TestGame) {
                game.declareAttackers(mapOf("Throat Slitter" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers().error shouldBe null
                advanceToDecision(game)
            }

            test("combat damage destroys a nonblack creature the damaged player controls") {
                val game = combatBoard().build()
                swingUnblocked(game)

                val bears = game.findPermanent("Grizzly Bears")!!
                val select = game.selectTargets(listOf(bears))
                withClue("a nonblack creature the damaged player controls is legal: ${select.error}") {
                    select.error shouldBe null
                }
                game.resolveStack()

                withClue("Grizzly Bears was destroyed") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }

            test("black creatures the damaged player controls are not legal targets") {
                val game = combatBoard().build()
                swingUnblocked(game)

                val blackCreature = game.findPermanent("Black Creature")!!
                val select = game.selectTargets(listOf(blackCreature))
                withClue("a black creature must be rejected as a target") {
                    select.error shouldNotBe null
                }
                withClue("Black Creature stays on the battlefield") {
                    game.isOnBattlefield("Black Creature") shouldBe true
                }
            }
        }
    }
}
