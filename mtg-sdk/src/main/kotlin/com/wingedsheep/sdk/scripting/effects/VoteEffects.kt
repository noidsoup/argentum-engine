package com.wingedsheep.sdk.scripting.effects

import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.text.TextReplacer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Pipeline collection holding the vote options (objects players may vote for). */
const val COUNCIL_OPTIONS = "councilOptions"

/** Pipeline collection holding every cast vote (one entity id per vote). */
const val COUNCIL_VOTES = "councilVotes"

/** Pipeline collection holding the winning option(s) — most votes, including ties (CR 701.38). */
const val COUNCIL_WINNERS = "councilWinners"

/**
 * Non-secret vote (CR 701.38): each player, starting with [startingPlayer] and proceeding in turn
 * order, chooses exactly one option from [from]. Votes are public — later voters see earlier ones.
 *
 * When every player has voted, every option in [from] that received the greatest number of votes
 * (ties included) is written to [storeWinnersAs]. All cast votes land in [storeVotesAs].
 *
 * A no-op when [from] is empty or missing: no player is prompted and both output collections stay
 * empty.
 *
 * Object voting ("vote for an artifact, creature, or enchantment card in your graveyard") is the
 * intended use. Word-based council options ("time or knowledge") compose [ChooseOptionEffect] in a
 * per-player loop instead.
 */
@SerialName("Vote")
@Serializable
data class VoteEffect(
    val from: String,
    val storeVotesAs: String = COUNCIL_VOTES,
    val storeWinnersAs: String = COUNCIL_WINNERS,
    /** Who votes first — defaults to the ability's controller ("starting with you"). */
    val startingPlayer: Player = Player.You,
    val prompt: String? = null,
) : Effect {
    override val description: String =
        prompt ?: "Starting with ${startingPlayer.description}, each player votes for a choice"

    override fun applyTextReplacement(replacer: TextReplacer): Effect = this
}
