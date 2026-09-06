package com.wingedsheep.engine.handlers

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.sdk.model.EntityId
import kotlinx.serialization.Serializable

/**
 * State carried through pipeline effect execution (Gather → Select → Move).
 *
 * This groups the fields that are only relevant during pipeline effect chains,
 * keeping [EffectContext] focused on core effect execution concerns.
 */
@Serializable
data class PipelineState(
    /** Named card collections for pipeline effects (GatherCards → SelectFromCollection → MoveCollection) */
    val storedCollections: Map<String, List<EntityId>> = emptyMap(),
    /** Named targets map for BoundVariable resolution (target name -> chosen target) */
    val namedTargets: Map<String, ChosenTarget> = emptyMap(),
    /** Named values chosen by the player during pipeline execution (e.g., creature type, color). */
    val chosenValues: Map<String, String> = emptyMap(),
    /** Named numeric values stored by pipeline effects (e.g., cards not drawn). */
    val storedNumbers: Map<String, Int> = emptyMap(),
    /**
     * Per-player tallies keyed by store name, then player id. Used when a [ForEachPlayerEffect]
     * records a per-iteration count that a later step maximizes across players (Windfall).
     */
    val storedPerPlayerNumbers: Map<String, Map<EntityId, Int>> = emptyMap(),
    /** Named string lists stored by pipeline effects (e.g., chosen creature types). */
    val storedStringLists: Map<String, List<String>> = emptyMap(),
    /**
     * Named lists of subtype sets produced by `GatherSubtypesEffect`. Each entry is
     * `List<Set<String>>` — one subtype set per source entity in the order they were
     * gathered. Consumed by `CardPredicate.HasSubtypeInEachStoredGroup`.
     */
    val storedSubtypeGroups: Map<String, List<Set<String>>> = emptyMap(),
    /** When inside a ForEachInGroupEffect, the current iteration entity. EffectTarget.Self resolves to this. */
    val iterationTarget: EntityId? = null
) {
    companion object {
        val EMPTY = PipelineState()

        fun mergePerPlayerNumbers(
            existing: Map<String, Map<EntityId, Int>>,
            update: Map<String, Map<EntityId, Int>>,
        ): Map<String, Map<EntityId, Int>> {
            if (update.isEmpty()) return existing
            val merged = existing.toMutableMap()
            for ((storeAs, playerCounts) in update) {
                merged[storeAs] = (existing[storeAs] ?: emptyMap()) + playerCounts
            }
            return merged
        }

        /**
         * Pipeline collection name under which a batch trigger seeds the entities it captured
         * (the matching permanents in a `PermanentsEnteredEvent` batch). Aliases the SDK-side
         * contract [com.wingedsheep.sdk.scripting.effects.IterationSpace.TRIGGER_CAPTURED_COLLECTION]
         * so card definitions (which only see the SDK) and the engine name the same collection.
         */
        const val TRIGGER_CAPTURED_COLLECTION =
            com.wingedsheep.sdk.scripting.effects.IterationSpace.TRIGGER_CAPTURED_COLLECTION
    }
}
