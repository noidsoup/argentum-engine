package com.wingedsheep.mtg.sets.definitions.fut.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Quiet Disrepair
 * {1}{G}
 * Enchantment — Aura
 *
 * Enchant artifact or enchantment
 * At the beginning of your upkeep, choose one —
 * • Destroy enchanted permanent.
 * • You gain 2 life.
 */
val QuietDisrepair = card("Quiet Disrepair") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant artifact or enchantment\n" +
        "At the beginning of your upkeep, choose one —\n" +
        "• Destroy enchanted permanent.\n" +
        "• You gain 2 life."

    auraTarget = Targets.ArtifactOrEnchantment

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = ModalEffect.chooseOne(
            Mode.noTarget(
                Effects.Destroy(EffectTarget.EnchantedPermanent),
                "Destroy enchanted permanent."
            ),
            Mode.noTarget(
                Effects.GainLife(2, EffectTarget.Controller),
                "You gain 2 life."
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Glen Angus"
        flavorText = "\"Artifice has always stood upon nature's shoulders. Let us watch nature take a turn.\"\n—Freyalise"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/541ce95e-5102-44fb-b74d-7469331ca9cc.jpg?1783943097"
    }
}
