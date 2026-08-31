package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModeOption
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.conditions.SourceChosenModeIs
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Primal Plasma
 * {3}{U}
 * Creature — Elemental Shapeshifter
 * Power/toughness: star/star
 *
 * As this creature enters, it becomes your choice of a 3/3 creature, a 2/2 creature with flying,
 * or a 1/6 creature with defender.
 *
 * Same EntersWithChoice + SourceChosenModeIs + SetBasePowerToughnessStatic pattern as Primal Clay,
 * without artifact typing or the Wall subtype on the defender mode (oracle: "1/6 creature with
 * defender", not Wall).
 */
val PrimalPlasma = card("Primal Plasma") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Shapeshifter"
    power = 0
    toughness = 0
    oracleText = "As this creature enters, it becomes your choice of a 3/3 creature, a 2/2 creature " +
        "with flying, or a 1/6 creature with defender. (A creature with defender can't attack.)"

    replacementEffect(
        EntersWithChoice(
            choiceType = ChoiceType.MODE,
            modeOptions = listOf(
                ModeOption(id = "3/3", label = "3/3 creature"),
                ModeOption(id = "2/2 flying", label = "2/2 creature with flying"),
                ModeOption(id = "1/6 defender", label = "1/6 creature with defender"),
            ),
        ),
    )

    staticAbility {
        condition = SourceChosenModeIs("3/3")
        ability = SetBasePowerToughnessStatic(power = 3, toughness = 3, filter = GroupFilter.source())
    }

    staticAbility {
        condition = SourceChosenModeIs("2/2 flying")
        ability = SetBasePowerToughnessStatic(power = 2, toughness = 2, filter = GroupFilter.source())
    }
    staticAbility {
        condition = SourceChosenModeIs("2/2 flying")
        ability = GrantKeyword(keyword = Keyword.FLYING, filter = GroupFilter.source())
    }

    staticAbility {
        condition = SourceChosenModeIs("1/6 defender")
        ability = SetBasePowerToughnessStatic(power = 1, toughness = 6, filter = GroupFilter.source())
    }
    staticAbility {
        condition = SourceChosenModeIs("1/6 defender")
        ability = GrantKeyword(keyword = Keyword.DEFENDER, filter = GroupFilter.source())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "Luca Zontini"
        flavorText = "Tocasia brushed the gears and cogs from the table. There, before two wide-eyed " +
            "brothers, she began a lesson on raw elemental magic."
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2bbdd96b-e740-43d0-a1b6-7b9b92cc7993.jpg?1783943158"
    }
}
