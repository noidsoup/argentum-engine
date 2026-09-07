package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ToughnessAtMostDynamicSerializationTest : FunSpec({

    test("GameObjectFilter.toughnessAtMostDynamic serializes with the predicate type tag") {
        val amount = DynamicAmounts.battlefield(
            Player.You,
            GameObjectFilter.Land.withSubtype(Subtype.ISLAND),
        ).count()
        val filter = GameObjectFilter.Creature.toughnessAtMostDynamic(amount)

        filter.cardPredicates shouldContain CardPredicate.ToughnessAtMostDynamic(amount)

        val json = Json.encodeToString(filter)
        json shouldContain "ToughnessAtMostDynamic"
        json shouldContain "AggregateBattlefield"

        Json.decodeFromString<GameObjectFilter>(json) shouldBe filter
    }
})
