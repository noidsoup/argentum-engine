package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WithoutManaAbilitiesPredicateTest : FunSpec({

    val vanillaArtifact = card("Vanilla Artifact Predicate Test") {
        typeLine = "Artifact"
    }

    val manaRock = card("Mana Rock Predicate Test") {
        typeLine = "Artifact"
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.AddColorlessMana(1)
            manaAbility = true
        }
    }

    val nonManaArtifact = card("Non-Mana Artifact Predicate Test") {
        typeLine = "Artifact"
        activatedAbility {
            cost = Costs.Mana("{2}")
            effect = Effects.DrawCards(1)
        }
    }

    test("CardDefinition.hasManaActivatedAbility reflects printed mana abilities only") {
        vanillaArtifact.hasManaActivatedAbility shouldBe false
        manaRock.hasManaActivatedAbility shouldBe true
        nonManaArtifact.hasManaActivatedAbility shouldBe false
    }

    test("GameObjectFilter.withoutManaAbilities() carries the predicate") {
        val filter = GameObjectFilter.Artifact.withoutManaAbilities()
        filter.cardPredicates shouldBe listOf(
            CardPredicate.IsArtifact,
            CardPredicate.WithoutManaAbilities,
        )
        filter.description shouldBe "artifact without mana abilities"
    }

    test("CardPredicate.WithoutManaAbilities round-trips through serialization") {
        val filter = GameObjectFilter.Artifact.withoutManaAbilities()
        val json = Json.encodeToString(filter)
        json shouldContain "WithoutManaAbilities"
        Json.decodeFromString<GameObjectFilter>(json) shouldBe filter
    }
})
