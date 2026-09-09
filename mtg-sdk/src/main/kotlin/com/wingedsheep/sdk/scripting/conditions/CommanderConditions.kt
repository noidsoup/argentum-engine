package com.wingedsheep.sdk.scripting.conditions

import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.text.TextReplacer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * True when [player] controls a commander — any card carrying the engine's
 * `CommanderComponent`, whether in that player's command zone or on the battlefield under their
 * control (CR 108.3).
 *
 * "Any player's commander" satisfies the check: a player who gained control of another player's
 * commander until end of turn still controls it during the end step (Crimson Honor Guard ruling,
 * 2017-08-25). Commanders in the graveyard, exile, hand, or library do not count — only the
 * command zone and the battlefield are searched.
 *
 * Evaluated at resolution time only; [Player.TriggeringPlayer] and other trigger-relative
 * references require an [EffectContext].
 */
@SerialName("PlayerControlsCommander")
@Serializable
data class PlayerControlsCommander(
    val player: Player = Player.You,
) : Condition {
    override val description: String =
        "if ${player.description} control${if (player == Player.You) "" else "s"} a commander"

    override fun applyTextReplacement(replacer: TextReplacer): Condition = this
}
