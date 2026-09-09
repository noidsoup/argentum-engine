package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.TokenComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.effects.MoveToZoneEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.core.ManaCost
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Mechanic coverage for Timothar, Baron of Bats — pay {1} to exile a dying nontoken Vampire from
 * the graveyard, create a Bat token linked to that exile, and return the exiled card tapped when
 * the Bat deals combat damage to a player.
 *
 * Timothar itself is not authored here; these tests pin [MoveToZoneEffect.linkToTarget],
 * [Effects.ReturnLinkedExileTappedUnderOwnersControl], and the composite shape the card will use.
 */
class TimotharBaronOfBatsMechanicTest : FunSpec({

    val projector = StateProjector()

    val batReturnTrigger = TriggeredAbility.create(
        trigger = Triggers.DealsCombatDamageToPlayer.event,
        binding = Triggers.DealsCombatDamageToPlayer.binding,
        effect = CompositeEffect(
            listOf(
                SacrificeSelfEffect,
                Effects.ReturnLinkedExileTappedUnderOwnersControl(),
            )
        ),
        descriptionOverride = "When this token deals combat damage to a player, sacrifice it and " +
            "return the exiled card to the battlefield tapped.",
    )

    val BaronProbe = card("Baron Probe") {
        manaCost = "{2}{B}"
        typeLine = "Legendary Creature — Vampire Noble"
        power = 4
        toughness = 4
        oracleText = "Whenever another nontoken Vampire you control dies, you may pay {1} and exile " +
            "it. If you do, create a 1/1 black Bat creature token with flying. It gains \"When this " +
            "token deals combat damage to a player, sacrifice it and return the exiled card to the " +
            "battlefield tapped.\""

        triggeredAbility {
            trigger = TriggerSpec(
                event = ZoneChangeEvent(
                    filter = GameObjectFilter.Creature
                        .withSubtype(Subtype.VAMPIRE)
                        .youControl()
                        .nontoken(),
                    from = Zone.BATTLEFIELD,
                    to = Zone.GRAVEYARD,
                ),
                binding = TriggerBinding.OTHER,
            )
            effect = MayPayManaEffect(
                ManaCost.parse("{1}"),
                CompositeEffect(
                    listOf(
                        CreateTokenEffect(
                            power = 1,
                            toughness = 1,
                            colors = setOf(Color.BLACK),
                            creatureTypes = setOf("Bat"),
                            keywords = setOf(Keyword.FLYING),
                            triggeredAbilities = listOf(batReturnTrigger),
                        ),
                        MoveToZoneEffect(
                            target = EffectTarget.TriggeringEntity,
                            destination = Zone.EXILE,
                            fromZone = Zone.GRAVEYARD,
                            linkToTarget = EffectTarget.PipelineTarget(CREATED_TOKENS, 0),
                        ),
                    )
                ),
            )
        }
    }

    val VampireFodder = card("Vampire Fodder") {
        // Red so Doom Blade ({1}{B}, nonblack only) can destroy it in tests.
        manaCost = "{1}{R}"
        typeLine = "Creature — Vampire"
        power = 2
        toughness = 2
        oracleText = ""
    }

    val HumanFodder = card("Human Fodder") {
        manaCost = "{1}{W}"
        typeLine = "Creature — Human"
        power = 2
        toughness = 2
        oracleText = ""
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(BaronProbe, VampireFodder, HumanFodder))
        return d
    }

    fun resolveStack(driver: GameTestDriver) {
        var guard = 0
        while (guard++ < 30 && (driver.state.stack.isNotEmpty() || driver.isPaused)) {
            when (val decision = driver.pendingDecision) {
                is com.wingedsheep.engine.core.YesNoDecision ->
                    driver.submitYesNo(decision.playerId, true)
                is com.wingedsheep.engine.core.SelectManaSourcesDecision ->
                    driver.submitManaAutoPayOrDecline(decision.playerId, true)
                else -> driver.bothPass()
            }
        }
    }

    fun batTokens(driver: GameTestDriver, player: EntityId): List<EntityId> =
        driver.getCreatures(player).filter { id ->
            val entity = driver.state.getEntity(id)
            entity?.get<TokenComponent>() != null &&
                entity.get<com.wingedsheep.engine.state.components.identity.CardComponent>()?.name == "Bat Token"
        }

    test("paying {1} when another nontoken Vampire dies exiles it and creates a linked Bat token") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Baron Probe")
        val vampire = driver.putCreatureOnBattlefield(player, "Vampire Fodder")
        val doom = driver.putCardInHand(player, "Doom Blade")
        driver.giveMana(player, Color.BLACK, 3)

        val cast = driver.castSpell(player, doom, targets = listOf(vampire))
        cast.error shouldBe null
        driver.bothPass() // Doom Blade resolves; dies trigger on stack
        resolveStack(driver)

        driver.getExile(player).shouldContain(vampire)

        val bats = batTokens(driver, player)
        bats.size shouldBe 1
        val bat = bats.single()
        driver.state.getEntity(bat)?.get<LinkedExileComponent>()?.exiledIds shouldBe listOf(vampire)

        val projected = projector.project(driver.state)
        projected.hasKeyword(bat, Keyword.FLYING) shouldBe true
    }

    test("declining the {1} payment does not exile or create a Bat") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Baron Probe")
        val vampire = driver.putCreatureOnBattlefield(player, "Vampire Fodder")
        val doom = driver.putCardInHand(player, "Doom Blade")
        // {1}{B} to cast Doom Blade; leave {1} in the pool so the may-pay prompt still appears.
        driver.giveMana(player, Color.BLACK, 3)

        driver.castSpell(player, doom, targets = listOf(vampire)).error shouldBe null
        driver.bothPass() // Doom Blade resolves; dies trigger on stack
        driver.bothPass() // trigger begins resolving → may-pay prompt
        val decision = driver.pendingDecision as? com.wingedsheep.engine.core.YesNoDecision
        decision.shouldNotBeNull()
        driver.submitYesNo(decision.playerId, false)
        driver.bothPass()

        driver.getGraveyard(player).shouldContain(vampire)
        driver.getExile(player).shouldBeEmpty()
        batTokens(driver, player).size shouldBe 0
    }

    test("non-Vampire deaths do not offer the pay-and-exile rider") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Baron Probe")
        val human = driver.putCreatureOnBattlefield(player, "Human Fodder")
        val doom = driver.putCardInHand(player, "Doom Blade")
        driver.giveMana(player, Color.BLACK, 1)

        driver.castSpell(player, doom, targets = listOf(human))
        driver.bothPass()
        driver.bothPass()

        driver.getExile(player).shouldBeEmpty()
        batTokens(driver, player).size shouldBe 0
    }

    test("Bat combat damage sacrifices the token and returns the exiled Vampire tapped") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingLife = 20)
        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(player, "Baron Probe")
        val vampire = driver.putCreatureOnBattlefield(player, "Vampire Fodder")
        val doom = driver.putCardInHand(player, "Doom Blade")
        driver.giveMana(player, Color.BLACK, 3)

        driver.castSpell(player, doom, targets = listOf(vampire)).error shouldBe null
        driver.bothPass()
        resolveStack(driver)

        val bat = batTokens(driver, player).single()
        driver.passPriorityUntil(Step.END)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(bat), opponent).error shouldBe null
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)
        resolveStack(driver)

        batTokens(driver, player).size shouldBe 0
        val returned = driver.findPermanent(player, "Vampire Fodder")
        returned.shouldNotBeNull()
        driver.state.getEntity(returned)?.has<TappedComponent>() shouldBe true
        driver.getExile(player).shouldBeEmpty()
    }
})
