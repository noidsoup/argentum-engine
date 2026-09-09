package com.wingedsheep.engine.handlers.effects.library

import com.wingedsheep.engine.core.DecisionPhase
import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.core.VoteContinuation
import com.wingedsheep.engine.handlers.DecisionHandler
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.continuations.exposeCollectionsToNextFrame
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.TargetResolutionUtils
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.VoteEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import kotlin.reflect.KClass

/**
 * Executor for [VoteEffect] (CR 701.38).
 *
 * Each player votes in turn order starting with the resolved [VoteEffect.startingPlayer]
 * (default: the ability's controller — "starting with you"). Votes are public: later voters see
 * earlier choices in the prompt. When every player has voted, options with the greatest vote
 * count — ties included — are written to [VoteEffect.storeWinnersAs].
 */
class VoteExecutor(
    private val decisionHandler: DecisionHandler = DecisionHandler(),
) : EffectExecutor<VoteEffect> {

    override val effectType: KClass<VoteEffect> = VoteEffect::class

    override fun execute(state: GameState, effect: VoteEffect, context: EffectContext): EffectResult {
        val options = context.pipeline.storedCollections[effect.from] ?: emptyList()
        if (options.isEmpty()) {
            return EffectResult.success(state).copy(
                updatedCollections = mapOf(
                    effect.storeVotesAs to emptyList(),
                    effect.storeWinnersAs to emptyList(),
                )
            )
        }

        val startPlayer = resolveStartingPlayer(state, effect.startingPlayer, context)
            ?: return EffectResult.error(state, "Could not resolve vote starting player")

        val order = turnOrderStartingWith(state, startPlayer)
        if (order.isEmpty()) {
            return EffectResult.success(state).copy(
                updatedCollections = mapOf(
                    effect.storeVotesAs to emptyList(),
                    effect.storeWinnersAs to emptyList(),
                )
            )
        }

        return askPlayerToVote(
            state = state,
            effect = effect,
            context = context,
            options = options,
            currentPlayer = order.first(),
            remainingPlayers = order.drop(1),
            votes = emptyList(),
        )
    }

    internal fun askPlayerToVote(
        state: GameState,
        effect: VoteEffect,
        context: EffectContext,
        options: List<EntityId>,
        currentPlayer: EntityId,
        remainingPlayers: List<EntityId>,
        votes: List<EntityId>,
    ): EffectResult {
        val sourceName = context.sourceId?.let { state.getEntity(it)?.get<CardComponent>()?.name }

        val totalVoters = votes.size + remainingPlayers.size + 1
        val prompt = buildVotePrompt(
            state = state,
            effect = effect,
            votes = votes,
            voteIndex = votes.size + 1,
            totalVoters = totalVoters,
        )

        val continuation = VoteContinuation(
            effect = effect,
            options = options,
            currentPlayerId = currentPlayer,
            remainingPlayers = remainingPlayers,
            votes = votes,
            sourceId = context.sourceId,
            sourceName = sourceName,
            controllerId = context.controllerId,
            storedCollections = context.pipeline.storedCollections,
            effectContext = context,
            objectReferences = context.objectReferences,
        )

        val decisionResult = decisionHandler.createCardSelectionDecision(
            state = state,
            playerId = currentPlayer,
            sourceId = context.sourceId,
            sourceName = sourceName,
            prompt = prompt,
            options = options,
            minSelections = 1,
            maxSelections = 1,
            phase = DecisionPhase.RESOLUTION,
            answer = continuation,
        )

        return EffectResult.propagatePause(decisionResult.state, decisionResult.events)
    }

    internal fun completeVote(
        state: GameState,
        effect: VoteEffect,
        options: List<EntityId>,
        votes: List<EntityId>,
    ): Map<String, List<EntityId>> {
        val winners = if (votes.isEmpty()) {
            emptyList()
        } else {
            val tally = votes.groupingBy { it }.eachCount()
            val maxVotes = tally.values.max()
            options.filter { tally[it] == maxVotes }
        }

        return mapOf(
            effect.storeVotesAs to votes,
            effect.storeWinnersAs to winners,
        )
    }

    companion object {
        fun resolveStartingPlayer(
            state: GameState,
            startingPlayer: Player,
            context: EffectContext,
        ): EntityId? = when (startingPlayer) {
            Player.You -> context.controllerId
            Player.ActivePlayerFirst -> state.activePlayerId
            else -> TargetResolutionUtils
                .resolvePlayerTargets(EffectTarget.PlayerRef(startingPlayer), state, context)
                .firstOrNull()
        }

        /** Active players in turn order, rotated so [startPlayer] votes first (CR 701.38a). */
        fun turnOrderStartingWith(state: GameState, startPlayer: EntityId): List<EntityId> {
            val ordered = state.activePlayers
            val index = ordered.indexOf(startPlayer)
            return if (index < 0) ordered else ordered.drop(index) + ordered.take(index)
        }

        fun buildVotePrompt(
            state: GameState,
            effect: VoteEffect,
            votes: List<EntityId>,
            voteIndex: Int,
            totalVoters: Int,
        ): String {
            val base = effect.prompt
                ?: "Vote for a card ($voteIndex of $totalVoters)"
            if (votes.isEmpty()) return base

            val prior = votes.mapIndexed { index, cardId ->
                val cardName = state.getEntity(cardId)?.get<CardComponent>()?.name ?: "a card"
                "Vote ${index + 1}: $cardName"
            }.joinToString("; ")
            return "$base. Prior votes: $prior"
        }
    }
}

/**
 * Resume a [VoteEffect] after each player's selection.
 */
fun resumeVote(
    state: GameState,
    continuation: VoteContinuation,
    chosenCard: EntityId,
    checkForMore: (GameState, List<GameEvent>) -> ExecutionResult,
): ExecutionResult {
    val effect = continuation.effect
    val options = continuation.options

    if (chosenCard !in options) {
        return ExecutionResult.error(state, "Chosen card is not a valid vote option")
    }

    val newVotes = continuation.votes + chosenCard
    val context = continuation.effectContext
        ?: EffectContext(
            sourceId = continuation.sourceId,
            controllerId = continuation.controllerId,
        )

    if (continuation.remainingPlayers.isNotEmpty()) {
        val nextPlayer = continuation.remainingPlayers.first()
        val executor = VoteExecutor()
        val result = executor.askPlayerToVote(
            state = state,
            effect = effect,
            context = context,
            options = options,
            currentPlayer = nextPlayer,
            remainingPlayers = continuation.remainingPlayers.drop(1),
            votes = newVotes,
        )
        if (result.pendingDecision != null) {
            return ExecutionResult.propagatePause(result.state, result.events)
        }
        if (result.error != null) return result.toExecutionResult()
    }

    val collections = VoteExecutor().completeVote(state, effect, options, newVotes)
    val exposedState = exposeCollectionsToNextFrame(state, collections)
    return checkForMore(exposedState, emptyList())
}
