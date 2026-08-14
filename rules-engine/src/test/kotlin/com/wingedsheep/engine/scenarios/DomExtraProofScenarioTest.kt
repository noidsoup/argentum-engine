package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ogw.cards.MeanderingRiver
import com.wingedsheep.mtg.sets.definitions.ogw.cards.TimberGorge
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.Rarity
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * DOM Extra campaign scenarios (proof + second batch).
 */
class DomExtraProofScenarioTest : ScenarioTestBase() {

    // Minimal Chandra planeswalker so Karplusan Hound's intervening-if can be exercised.
    private val testChandra = card("Chandra, Test Fixture") {
        manaCost = "{4}{R}{R}"
        colorIdentity = "R"
        typeLine = "Legendary Planeswalker — Chandra"
        startingLoyalty = 4
        oracleText = "0: Draw a card."
        loyaltyAbility(0) {
            effect = Effects.DrawCards(1)
        }
        metadata {
            rarity = Rarity.MYTHIC
            collectorNumber = "999"
            artist = "Test"
        }
    }

    init {
        cardRegistry.register(testChandra)

        context("Pyromantic Pilgrim") {
            test("enters with haste") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Pyromantic Pilgrim")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val pilgrim = game.findPermanent("Pyromantic Pilgrim")!!
                withClue("Pyromantic Pilgrim should have haste") {
                    game.state.projectedState.hasKeyword(pilgrim, Keyword.HASTE) shouldBe true
                }
                withClue("Pyromantic Pilgrim should be a 3/1") {
                    game.state.projectedState.getPower(pilgrim) shouldBe 3
                    game.state.projectedState.getToughness(pilgrim) shouldBe 1
                }
            }
        }

        context("Temporal Machinations") {
            test("bounces the target creature without drawing when you control no artifact") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Temporal Machinations")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Temporal Machinations", targetId = bears).error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("Grizzly Bears returned to owner's hand") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.isInHand(2, "Grizzly Bears") shouldBe true
                }
                withClue("No draw when caster controls no artifact (hand only lost the spell)") {
                    game.handSize(1) shouldBe handBefore - 1
                }
            }

            test("bounces the target and draws a card when you control an artifact") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Temporal Machinations")
                    .withCardOnBattlefield(1, "Sol Ring")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(1, "Mountain")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Temporal Machinations", targetId = bears).error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("Grizzly Bears returned to owner's hand") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.isInHand(2, "Grizzly Bears") shouldBe true
                }
                withClue("Drew one card after bouncing while controlling an artifact") {
                    game.handSize(1) shouldBe handBefore
                    game.isInHand(1, "Mountain") shouldBe true
                }
            }
        }

        context("Teferi's Sentinel") {
            test("is 2/6 without a Teferi planeswalker") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Teferi's Sentinel")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sentinel = game.findPermanent("Teferi's Sentinel")!!
                game.state.projectedState.getPower(sentinel) shouldBe 2
                game.state.projectedState.getToughness(sentinel) shouldBe 6
            }

            test("gets +4/+0 while you control a Teferi planeswalker") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Teferi's Sentinel")
                    .withCardOnBattlefield(1, "Teferi, Hero of Dominaria")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sentinel = game.findPermanent("Teferi's Sentinel")!!
                withClue("2/6 +4/+0 = 6/6 with Teferi") {
                    game.state.projectedState.getPower(sentinel) shouldBe 6
                    game.state.projectedState.getToughness(sentinel) shouldBe 6
                }
            }
        }

        context("Karplusan Hound") {
            test("does not trigger when you control no Chandra planeswalker") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Karplusan Hound", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Karplusan Hound" to 2)).error shouldBe null

                withClue("Intervening-if failed — no damage trigger on the stack") {
                    game.hasPendingDecision() shouldBe false
                    game.getLifeTotal(2) shouldBe 20
                }
            }

            test("deals 2 damage to any target when attacking with a Chandra") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Karplusan Hound", summoningSickness = false)
                    .withCardOnBattlefield(1, "Chandra, Test Fixture")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Karplusan Hound" to 2)).error shouldBe null

                // Target the defending player for 2 damage.
                if (game.getPendingDecision() is ChooseTargetsDecision) {
                    game.selectTargets(listOf(game.player2Id))
                }
                game.resolveStack()

                withClue("Opponent took 2 from the Hound trigger") {
                    game.getLifeTotal(2) shouldBe 18
                }
            }
        }
    }
}

class DomExtraLandsScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + MeanderingRiver + TimberGorge)
        return driver
    }

    test("Meandering River enters tapped") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putCardInHand(p1, "Meandering River")
        driver.playLand(p1, land).isSuccess shouldBe true
        driver.state.getEntity(land)?.has<TappedComponent>() shouldBe true
    }

    test("Timber Gorge enters tapped") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val land = driver.putCardInHand(p1, "Timber Gorge")
        driver.playLand(p1, land).isSuccess shouldBe true
        driver.state.getEntity(land)?.has<TappedComponent>() shouldBe true
    }
})
