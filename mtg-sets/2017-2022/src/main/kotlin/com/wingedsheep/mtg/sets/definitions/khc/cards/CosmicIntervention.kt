package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Cosmic Intervention — Kaldheim Commander (KHC) #3
 * {3}{W} · Instant
 *
 * If a permanent you control would be put into a graveyard from the battlefield this turn, exile it
 * instead. Return it to the battlefield under its owner's control at the beginning of the next end
 * step.
 * Foretell {1}{W}
 *
 * The exile-and-return rider is [Effects.GrantExileInsteadOfDeathFromBattlefieldWithReturn] — a
 * turn-duration [GrantReplacementEffect] over battlefield→graveyard that schedules a delayed
 * return when the redirect applies (replacement, so dies triggers do not fire).
 *
 * Foretell is display-only as [Keyword.FORETELL]; cast/exile wiring is [KeywordAbility.foretell]
 * (Doomskar / Saw It Coming).
 */
val CosmicIntervention = card("Cosmic Intervention") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "If a permanent you control would be put into a graveyard from the battlefield " +
        "this turn, exile it instead. Return it to the battlefield under its owner's control at " +
        "the beginning of the next end step.\n" +
        "Foretell {1}{W} (During your turn, you may pay {2} and exile this card from your hand " +
        "face down. Cast it on a later turn for its foretell cost.)"

    spell {
        effect = Effects.GrantExileInsteadOfDeathFromBattlefieldWithReturn()
    }

    keywordAbility(KeywordAbility.foretell("{1}{W}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "3"
        artist = "Alexander Mokhov"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/933c4a54-5c3a-496b-aa35-edf791155d8d.jpg?1783928340"
    }
}
