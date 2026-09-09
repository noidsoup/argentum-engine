package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.GrantAdditionalTypesToGroup
import com.wingedsheep.sdk.scripting.ModifyStatsOnCreaturesEquippedTo
import com.wingedsheep.sdk.scripting.StaticAbility
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Predefined-token recipes — Blood/Food/Clue creation counts and the "tokens you control are
 * Equipment … equip {N}" lord bundle (Arterial Alchemy and kin).
 *
 * Reached through [Patterns.Token].
 */
object TokenPatterns {

    /** Blood artifact tokens you control — the filter half of Arterial Alchemy's static line. */
    fun bloodTokensYouControl(): GroupFilter =
        GroupFilter(Filters.BloodToken.youControl())

    /**
     * "Create a Blood token for each opponent you have." — Arterial Alchemy's ETB line.
     */
    fun createBloodForEachOpponent(): Effect =
        Effects.CreateBlood(DynamicAmount.PlayerCount(Player.EachOpponent))

    /**
     * "{Tokens} you control are Equipment in addition to their other types and have
     * \"Equipped creature gets +P/+T\" and equip {cost}."
     *
     * Returns three static abilities to register as separate `staticAbility { }` blocks (or fold
     * into one card via three calls).
     */
    fun grantAsEquipmentWithPumpAndEquip(
        tokenFilter: GroupFilter,
        powerBonus: Int,
        toughnessBonus: Int,
        equipCost: ManaCost,
    ): List<StaticAbility> = listOf(
        GrantAdditionalTypesToGroup(
            filter = tokenFilter,
            addSubtypes = listOf("Equipment"),
        ),
        ModifyStatsOnCreaturesEquippedTo(
            equipmentFilter = tokenFilter,
            powerBonus = powerBonus,
            toughnessBonus = toughnessBonus,
        ),
        GrantActivatedAbility(
            ability = ActivatedAbility.equip(equipCost),
            filter = tokenFilter,
        ),
    )

    /**
     * Arterial Alchemy's Blood-token equipment bundle — shorthand over [grantAsEquipmentWithPumpAndEquip].
     */
    fun grantBloodTokensAsEquipment(
        powerBonus: Int = 2,
        toughnessBonus: Int = 0,
        equipCost: ManaCost = ManaCost.parse("{2}"),
    ): List<StaticAbility> = grantAsEquipmentWithPumpAndEquip(
        tokenFilter = bloodTokensYouControl(),
        powerBonus = powerBonus,
        toughnessBonus = toughnessBonus,
        equipCost = equipCost,
    )
}
