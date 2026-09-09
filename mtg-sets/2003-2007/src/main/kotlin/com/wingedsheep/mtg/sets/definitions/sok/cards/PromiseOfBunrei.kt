package com.wingedsheep.mtg.sets.definitions.sok.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Promise of Bunrei (Saviors of Kamigawa #24)
 * {2}{W}
 * Enchantment
 *
 * When a creature you control dies, sacrifice this enchantment. If you do, create four 1/1
 * colorless Spirit creature tokens.
 *
 * The die trigger is [Triggers.YourCreatureDies]. Sacrificing the enchantment is mandatory when
 * the trigger resolves; [Effects.IfYouDo] with [SuccessCriterion.PermanentsSacrificed] gates the
 * four Spirit tokens on the sacrifice actually happening (for example, if the enchantment left
 * the battlefield before resolution).
 */
val PromiseOfBunrei = card("Promise of Bunrei") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "When a creature you control dies, sacrifice this enchantment. If you do, " +
        "create four 1/1 colorless Spirit creature tokens."

    triggeredAbility {
        trigger = Triggers.YourCreatureDies
        effect = Effects.IfYouDo(
            action = Effects.SacrificeTarget(EffectTarget.Self),
            ifYouDo = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = emptySet(),
                creatureTypes = setOf("Spirit"),
                count = 4
            ),
            successCriterion = SuccessCriterion.PermanentsSacrificed
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "24"
        artist = "Stephen Tappin"
        flavorText = "\"I am not afraid to die today nor afraid of what death will bring.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/2/1250735d-d43b-488f-bddf-ed10261a6382.jpg?1783944168"
    }
}
