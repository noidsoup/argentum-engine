package com.wingedsheep.engine.mechanics.bloodthirst

import com.wingedsheep.engine.mechanics.mana.GrantedKeywordResolver
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.stack.SpellGrantedKeywordsComponent
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Bloodthirst
import com.wingedsheep.sdk.scripting.EntersWithCounters

/**
 * Synthesizes bloodthirst (CR 702.53) enters-with replacements at the entry seam.
 *
 * Printed [KeywordAbility.Numeric] instances, one-shot spell grants
 * ([SpellGrantedKeywordsComponent.keywordParameters]), and battlefield
 * [GrantKeywordToOwnSpells] lords each contribute a separate instance; the engine turns every
 * amount into an [EntersWithCounters] gated on [Bloodthirst.entryCondition].
 */
object BloodthirstSynthesis {

    fun syntheticEntries(
        state: GameState,
        entityId: EntityId,
        cardDef: CardDefinition,
        controllerId: EntityId,
        cardRegistry: CardRegistry?,
        /** True only when a spell is resolving onto the battlefield — [GrantKeywordToOwnSpells] is spell-only. */
        spellCastEntry: Boolean = false,
    ): List<EntersWithCounters> =
        allAmounts(state, entityId, cardDef, controllerId, cardRegistry, spellCastEntry)
            .map { Bloodthirst.entersWithCounters(it) }

    fun allAmounts(
        state: GameState,
        entityId: EntityId,
        cardDef: CardDefinition,
        controllerId: EntityId,
        cardRegistry: CardRegistry?,
        spellCastEntry: Boolean = false,
    ): List<Int> {
        val amounts = mutableListOf<Int>()
        amounts.addAll(Bloodthirst.printedAmounts(cardDef))

        val spellGrant = state.getEntity(entityId)
            ?.get<SpellGrantedKeywordsComponent>()
            ?.keywordParameters
            ?.get(Keyword.BLOODTHIRST.name)
        if (spellGrant != null && spellGrant > 0) {
            amounts.add(spellGrant)
        }

        if (spellCastEntry && cardRegistry != null) {
            amounts.addAll(
                GrantedKeywordResolver(cardRegistry).bloodthirstAmountsFromLords(
                    state, controllerId, cardDef
                )
            )
        }
        return amounts
    }
}
