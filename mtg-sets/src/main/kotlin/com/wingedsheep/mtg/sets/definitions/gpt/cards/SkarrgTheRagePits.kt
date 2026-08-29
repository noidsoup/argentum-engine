package com.wingedsheep.mtg.sets.definitions.gpt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Skarrg, the Rage Pits
 * Land
 *
 * {T}: Add {C}.
 * {R}{G}, {T}: Target creature gets +1/+1 and gains trample until end of turn.
 */
val SkarrgTheRagePits = card("Skarrg, the Rage Pits") {
    typeLine = "Land"
    colorIdentity = "RG"
    oracleText = "{T}: Add {C}.\n{R}{G}, {T}: Target creature gets +1/+1 and gains trample until end of turn."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = AddColorlessManaEffect(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{R}{G}"),
            Costs.Tap,
        )
        val creature = target("target creature", TargetCreature())
        effect = Effects.ModifyStats(1, 1, creature)
            .then(Effects.GrantKeyword(Keyword.TRAMPLE, creature))
        description = "Target creature gets +1/+1 and gains trample until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "163"
        artist = "Martina Pilcerova"
        imageUri = "https://cards.scryfall.io/normal/front/3/4/342dcdde-e4cc-4a49-a818-9283002aba1a.jpg?1783943455"
    }
}
