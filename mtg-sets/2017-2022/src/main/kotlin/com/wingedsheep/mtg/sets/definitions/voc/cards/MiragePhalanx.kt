package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.soulbond
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CopyExceptions
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Mirage Phalanx
 * {4}{R}{R}
 * Creature — Human Soldier
 * 4/4
 *
 * Soulbond (You may pair this creature with another unpaired creature when either enters. They
 * remain paired for as long as you control both of them.)
 * As long as Mirage Phalanx is paired with another creature, each of those creatures has "At the
 * beginning of combat on your turn, create a token that's a copy of this creature, except it has
 * haste and loses soulbond. Exile it at end of combat."
 *
 * [GrantTriggeredAbility] over [GroupFilter.soulbondPair] — the same shape as Tandem Lookout and
 * the engine's [MiragePhalanxMechanicScenarioTest] reference card. [CopyExceptions] adds haste and
 * strips soulbond from the combat token; [Effects.CreateTokenCopyOfSelf.exileAtStep] handles exile
 * at end of combat.
 */
val MiragePhalanx = card("Mirage Phalanx") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Soldier"
    oracleText =
        "Soulbond (You may pair this creature with another unpaired creature when either enters. " +
            "They remain paired for as long as you control both of them.)\n" +
            "As long as Mirage Phalanx is paired with another creature, each of those creatures has " +
            "\"At the beginning of combat on your turn, create a token that's a copy of this creature, " +
            "except it has haste and loses soulbond. Exile it at end of combat.\""
    power = 4
    toughness = 4

    soulbond()

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.BeginCombat.event,
                binding = TriggerBinding.SELF,
                effect = Effects.CreateTokenCopyOfSelf(
                    exceptions = CopyExceptions(
                        addedKeywords = setOf(Keyword.HASTE),
                        removedKeywords = setOf(Keyword.SOULBOND),
                    ),
                    exileAtStep = Step.END_COMBAT,
                ),
                descriptionOverride =
                    "At the beginning of combat on your turn, create a token that's a copy of " +
                        "this creature, except it has haste and loses soulbond. Exile it at end of combat.",
            ),
            filter = GroupFilter.soulbondPair(),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "35"
        artist = "Scott Murphy"
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e67c7b3b-38c0-43ec-9cee-5605fc402af6.jpg?1783924995"
    }
}
