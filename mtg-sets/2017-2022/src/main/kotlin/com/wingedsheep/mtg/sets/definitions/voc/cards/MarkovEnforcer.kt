package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Markov Enforcer
 * {4}{R}{R}
 * Creature — Vampire Soldier
 * 6/6
 *
 * Whenever this creature or another Vampire you control enters, this creature fights up to one
 * target creature an opponent controls.
 * Whenever a creature dealt damage by this creature this turn dies, create a Blood token.
 *
 * Canonical printing: Innistrad: Crimson Vow Commander (VOC).
 *
 * "This creature or another Vampire you control enters" is one [TriggerBinding.ANY] enters trigger
 * over Vampires you control — the Kalastria Highborn / Cruel Celebrant idiom. The fight uses
 * [EffectTarget.Self] as the fighter with an optional opponent creature target (Novel Nunchaku
 * shape). The death trigger is [Triggers.CreatureDealtDamageByThisDies] + [Effects.CreateBlood].
 */
val MarkovEnforcer = card("Markov Enforcer") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire Soldier"
    oracleText = "Whenever this creature or another Vampire you control enters, this creature " +
        "fights up to one target creature an opponent controls.\n" +
        "Whenever a creature dealt damage by this creature this turn dies, create a Blood token. " +
        "(It's an artifact with \"{1}, {T}, Discard a card, Sacrifice this token: Draw a card.\")"
    power = 6
    toughness = 6

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE).youControl(),
            binding = TriggerBinding.ANY,
        )
        val foe = target(
            "up to one target creature an opponent controls",
            TargetCreature(optional = true, filter = TargetFilter.CreatureOpponentControls),
        )
        effect = Effects.Fight(EffectTarget.Self, foe)
        description = "Whenever this creature or another Vampire you control enters, this creature " +
            "fights up to one target creature an opponent controls."
    }

    triggeredAbility {
        trigger = Triggers.CreatureDealtDamageByThisDies
        effect = Effects.CreateBlood(1)
        description = "Whenever a creature dealt damage by this creature this turn dies, create a Blood token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "26"
        artist = "Wisnu Tan"
        imageUri = "https://cards.scryfall.io/normal/front/5/9/597bc4ae-335d-4d1d-8a57-530291abc540.jpg?1783924998"
    }
}
