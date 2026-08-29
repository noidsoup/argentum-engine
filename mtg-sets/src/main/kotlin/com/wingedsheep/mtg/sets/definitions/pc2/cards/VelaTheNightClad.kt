package com.wingedsheep.mtg.sets.definitions.pc2.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedExceptBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Vela the Night-Clad
 * {4}{U}{B}
 * Legendary Creature — Human Wizard
 * 4/4
 *
 * Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that
 * share a color with it.)
 * Other creatures you control have intimidate.
 * Whenever Vela or another creature you control leaves the battlefield, each opponent loses 1 life.
 *
 * Intimidate is modeled as [CantBeBlockedExceptBy] (artifact creatures or creatures sharing a
 * color with the intimidated permanent), not the display-only [com.wingedsheep.sdk.core.Keyword.INTIMIDATE]
 * enum entry.
 */
val VelaTheNightClad = card("Vela the Night-Clad") {
    manaCost = "{4}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Human Wizard"
    oracleText = "Intimidate (This creature can't be blocked except by artifact creatures and/or " +
        "creatures that share a color with it.)\n" +
        "Other creatures you control have intimidate.\n" +
        "Whenever Vela or another creature you control leaves the battlefield, each opponent loses 1 life."
    power = 4
    toughness = 4

    val intimidateBlockers =
        GameObjectFilter.ArtifactCreature or
            GameObjectFilter.Creature.sharingColorWith(EntityReference.Source)

    staticAbility {
        ability = CantBeBlockedExceptBy(blockerFilter = intimidateBlockers)
    }

    staticAbility {
        ability = CantBeBlockedExceptBy(
            blockerFilter = intimidateBlockers,
            filter = GroupFilter.OtherCreaturesYouControl,
        )
    }

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent))
        description = "Whenever Vela or another creature you control leaves the battlefield, each opponent loses 1 life."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "107"
        artist = "Allen Williams"
        flavorText = "She is the darkness that sails on midnight winds."
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e23b9df-2419-4906-8b21-29af6f65a2c3.jpg?1783940593"
    }
}
