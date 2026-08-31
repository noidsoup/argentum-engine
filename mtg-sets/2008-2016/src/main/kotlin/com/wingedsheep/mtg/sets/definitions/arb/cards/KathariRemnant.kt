package com.wingedsheep.mtg.sets.definitions.arb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kathari Remnant
 * {2}{U}{B}
 * Creature — Bird Skeleton
 * 0/1
 * Flying
 * {B}: Regenerate this creature.
 * Cascade
 */
val KathariRemnant = card("Kathari Remnant") {
    manaCost = "{2}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Bird Skeleton"
    oracleText =
        "Flying\n{B}: Regenerate this creature.\nCascade (When you cast this spell, exile cards " +
            "from the top of your library until you exile a nonland card that costs less. You may " +
            "cast it without paying its mana cost. Put the exiled cards on the bottom of your " +
            "library in a random order.)"
    power = 0
    toughness = 1
    keywords(Keyword.FLYING, Keyword.CASCADE)
    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }
    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = RegenerateEffect(EffectTarget.Self)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "23"
        artist = "Anthony S. Waters"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e27c56c6-fd72-4917-96d1-d7f9f8236fa2.jpg"
    }
}
