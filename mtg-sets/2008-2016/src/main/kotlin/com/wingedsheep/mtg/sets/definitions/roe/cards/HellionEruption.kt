package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Hellion Eruption
 * {5}{R}
 * Sorcery
 *
 * Sacrifice all creatures you control, then create that many 4/4 red Hellion creature tokens.
 *
 * The sacrifice batch records its moved set under `sacrificed` so the follow-up token count reads
 * `sacrificed_count` — the same storage pattern as Fumigate's `destroyed_count` life gain.
 */
val HellionEruption = card("Hellion Eruption") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Sacrifice all creatures you control, then create that many 4/4 red Hellion creature tokens."

    spell {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.BattlefieldMatching(filter = GameObjectFilter.Creature.youControl()),
                    storeAs = "hellionSacrifice_gathered",
                ),
                MoveCollectionEffect(
                    from = "hellionSacrifice_gathered",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD),
                    moveType = MoveType.Sacrifice,
                    storeMovedAs = "sacrificed",
                ),
                CreateTokenEffect(
                    count = DynamicAmount.VariableReference("sacrificed_count"),
                    power = 4,
                    toughness = 4,
                    colors = setOf(Color.RED),
                    creatureTypes = setOf("Hellion"),
                    imageUri = "https://cards.scryfall.io/normal/front/0/0/00a1209e-c4c3-4250-ad36-ef451f5e34cd.jpg?1783936802",
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "151"
        artist = "Anthony Francisco"
        flavorText = "Just when you thought you'd be safe, in the middle of an open field with no Eldrazi around for miles."
        imageUri = "https://cards.scryfall.io/normal/front/6/5/6529c92e-c79b-4953-8bd0-50ceae2ce261.jpg?1783941974"
    }
}
