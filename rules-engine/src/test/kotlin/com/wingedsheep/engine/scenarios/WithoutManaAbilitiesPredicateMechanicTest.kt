package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.legalactions.utils.TargetEnumerationUtils
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * Mechanic coverage for [CardPredicate.WithoutManaAbilities] and
 * [GameObjectFilter.withoutManaAbilities] — Midnight Arsonist's "destroy up to X target
 * artifacts without mana abilities, where X is the number of Vampires you control" shape.
 *
 * Inline probe cards only; the VOC printing ships in a follow-up add-card cycle.
 */
class WithoutManaAbilitiesPredicateMechanicTest : FunSpec({

    val vanillaArtifactProbe = card("Vanilla Artifact Probe") {
        manaCost = "{3}"
        typeLine = "Artifact"
        oracleText = "An artifact with no abilities."
    }

    val manaRockProbe = card("Mana Rock Probe") {
        manaCost = "{2}"
        typeLine = "Artifact"
        oracleText = "{T}: Add {C}."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.AddColorlessMana(1)
            manaAbility = true
        }
    }

    val nonManaArtifactProbe = card("Non-Mana Artifact Probe") {
        manaCost = "{2}"
        typeLine = "Artifact"
        oracleText = "{2}: Draw a card."
        activatedAbility {
            cost = Costs.Mana("{2}")
            effect = Effects.DrawCards(1)
        }
    }

    val dualAbilityArtifactProbe = card("Dual Ability Artifact Probe") {
        manaCost = "{3}"
        typeLine = "Artifact"
        oracleText = "{T}: Add {C}.\n{2}: Draw a card."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.AddColorlessMana(1)
            manaAbility = true
        }
        activatedAbility {
            cost = Costs.Mana("{2}")
            effect = Effects.DrawCards(1)
        }
    }

    val artifactWithoutManaFilter = GameObjectFilter.Artifact.withoutManaAbilities()

    fun driver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all +
                vanillaArtifactProbe +
                manaRockProbe +
                nonManaArtifactProbe +
                dualAbilityArtifactProbe,
        )
        return driver
    }

    fun matchesFilter(driver: GameTestDriver, playerId: EntityId, entityId: EntityId): Boolean =
        PredicateEvaluator().matches(
            driver.state,
            driver.state.projectedState,
            entityId,
            artifactWithoutManaFilter,
            PredicateContext(controllerId = playerId),
        )

    val destroyEligibleArtifacts = TargetPermanent(
        optional = true,
        dynamicMaxCount = DynamicAmount.AggregateBattlefield(
            Player.You,
            GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE),
        ),
        filter = TargetFilter(GameObjectFilter.Artifact.withoutManaAbilities()),
    )

    test("WithoutManaAbilities matches a vanilla artifact") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = driver.activePlayer!!
        val artifact = driver.putPermanentOnBattlefield(active, vanillaArtifactProbe.name)

        matchesFilter(driver, active, artifact) shouldBe true
    }

    test("WithoutManaAbilities rejects an artifact with only a mana ability") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = driver.activePlayer!!
        val artifact = driver.putPermanentOnBattlefield(active, manaRockProbe.name)

        matchesFilter(driver, active, artifact) shouldBe false
    }

    test("WithoutManaAbilities matches an artifact with only non-mana activated abilities") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = driver.activePlayer!!
        val artifact = driver.putPermanentOnBattlefield(active, nonManaArtifactProbe.name)

        matchesFilter(driver, active, artifact) shouldBe true
    }

    test("WithoutManaAbilities rejects an artifact that has any intrinsic mana ability") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = driver.activePlayer!!
        val artifact = driver.putPermanentOnBattlefield(active, dualAbilityArtifactProbe.name)

        matchesFilter(driver, active, artifact) shouldBe false
    }

    test("CardDefinition.hasManaActivatedAbility is precomputed from printed abilities") {
        vanillaArtifactProbe.hasManaActivatedAbility shouldBe false
        manaRockProbe.hasManaActivatedAbility shouldBe true
        nonManaArtifactProbe.hasManaActivatedAbility shouldBe false
        dualAbilityArtifactProbe.hasManaActivatedAbility shouldBe true
    }

    test("destroy targeting enumerates only artifacts without mana abilities") {
        val driver = driver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        val active = driver.activePlayer!!

        val vanilla = driver.putPermanentOnBattlefield(active, vanillaArtifactProbe.name)
        val nonMana = driver.putPermanentOnBattlefield(active, nonManaArtifactProbe.name)
        driver.putPermanentOnBattlefield(active, manaRockProbe.name)
        driver.putPermanentOnBattlefield(active, dualAbilityArtifactProbe.name)

        val legalTargets = TargetEnumerationUtils(PredicateEvaluator())
            .findValidTargets(driver.state, active, destroyEligibleArtifacts)

        legalTargets shouldContainExactlyInAnyOrder listOf(vanilla, nonMana)
    }
})
