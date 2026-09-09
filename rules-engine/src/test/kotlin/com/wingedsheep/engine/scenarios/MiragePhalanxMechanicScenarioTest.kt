package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.PairedComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.soulbond
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CopyExceptions
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Mirage Phalanx pattern (CR 702.95 + CR 707.9): while soulbond-paired, each half has "At the
 * beginning of combat on your turn, create a token that's a copy of this creature, except it has
 * haste and loses soulbond. Exile it at end of combat."
 *
 * Inline test card only — the VOC printing ships in a follow-up add-card cycle.
 */
class MiragePhalanxMechanicScenarioTest : ScenarioTestBase() {

    private val miragePhalanxPattern = card("Mirage Phalanx Pattern") {
        manaCost = "{3}{R}"
        colorIdentity = "R"
        typeLine = "Creature — Illusion Warrior"
        oracleText =
            "Soulbond (You may pair this creature with another unpaired creature when either enters. " +
                "They remain paired for as long as you control both of them.)\n" +
                "As long as Mirage Phalanx Pattern is paired with another creature, each of those " +
                "creatures has \"At the beginning of combat on your turn, create a token that's a " +
                "copy of this creature, except it has haste and loses soulbond. Exile it at end of combat.\""
        power = 2
        toughness = 2

        soulbond()

        staticAbility {
            ability = GrantTriggeredAbility(
                ability = TriggeredAbility.create(
                    trigger = Triggers.BeginCombat.event,
                    binding = TriggerBinding.SELF,
                    effect = Effects.CreateTokenCopyOfSelf(
                        exceptions = CopyExceptions(
                            addedKeywords = setOf(Keyword.HASTE),
                            removedKeywords = setOf(Keyword.SOULBOND),
                        ),
                        exileAtStep = Step.END_COMBAT,
                    ),
                    descriptionOverride =
                        "At the beginning of combat on your turn, create a token that's a copy of " +
                            "this creature, except it has haste and loses soulbond. Exile it at end of combat.",
                ),
                filter = GroupFilter.soulbondPair(),
            )
        }
    }

    init {
        cardRegistry.register(miragePhalanxPattern)

        context("Mirage Phalanx soulbond combat-copy pattern") {

            test("while paired, each half creates a haste token copy of itself at begin combat") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Mirage Phalanx Pattern", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val phalanx = game.findPermanent("Mirage Phalanx Pattern")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state = game.state
                    .updateEntity(phalanx) { it.with(PairedComponent(bears)) }
                    .updateEntity(bears) { it.with(PairedComponent(phalanx)) }

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                val phalanxCopies = game.findPermanents("Mirage Phalanx Pattern")
                val bearCopies = game.findPermanents("Grizzly Bears")
                withClue("each paired half mints one token copy of itself") {
                    phalanxCopies shouldHaveSize 2
                    bearCopies shouldHaveSize 2
                }

                val phalanxToken = phalanxCopies.first { it != phalanx }
                val bearToken = bearCopies.first { it != bears }
                withClue("the copies are tokens") {
                    game.state.getEntity(phalanxToken)?.has<TokenComponent>() shouldBe true
                    game.state.getEntity(bearToken)?.has<TokenComponent>() shouldBe true
                }
                withClue("except it has haste") {
                    game.state.projectedState.hasKeyword(phalanxToken, Keyword.HASTE) shouldBe true
                    game.state.projectedState.hasKeyword(bearToken, Keyword.HASTE) shouldBe true
                }
                withClue("except it loses soulbond") {
                    game.state.projectedState.hasKeyword(phalanxToken, Keyword.SOULBOND) shouldBe false
                    game.state.projectedState.hasKeyword(bearToken, Keyword.SOULBOND) shouldBe false
                }
            }

            test("the combat token is exiled at end of combat, not sacrificed") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Mirage Phalanx Pattern", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val phalanx = game.findPermanent("Mirage Phalanx Pattern")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state = game.state
                    .updateEntity(phalanx) { it.with(PairedComponent(bears)) }
                    .updateEntity(bears) { it.with(PairedComponent(phalanx)) }

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()
                game.findPermanents("Mirage Phalanx Pattern") shouldHaveSize 2

                game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                game.resolveStack()
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.resolveStack()

                withClue("only the original Phalanx remains after end of combat") {
                    game.findPermanents("Mirage Phalanx Pattern") shouldHaveSize 1
                    game.findPermanent("Mirage Phalanx Pattern") shouldNotBe null
                }
            }

            test("a token copy without soulbond does not pair when another creature enters") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Mirage Phalanx Pattern", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Savannah Lions")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val phalanx = game.findPermanent("Mirage Phalanx Pattern")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                game.state = game.state
                    .updateEntity(phalanx) { it.with(PairedComponent(bears)) }
                    .updateEntity(bears) { it.with(PairedComponent(phalanx)) }

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()
                val token = game.findPermanents("Mirage Phalanx Pattern").first { it != phalanx }

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)
                game.resolveStack()

                game.castSpell(1, "Savannah Lions").error shouldBe null
                game.resolveStack()

                withClue("the soulbond-less token should not be offered a pair") {
                    game.state.getEntity(token)?.get<PairedComponent>() shouldBe null
                }
            }

            test("unpaired Phalanx creates no combat tokens") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardOnBattlefield(1, "Mirage Phalanx Pattern", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                withClue("soulbondPair scope is empty while unpaired") {
                    game.findPermanents("Mirage Phalanx Pattern") shouldHaveSize 1
                }
            }
        }
    }
}
