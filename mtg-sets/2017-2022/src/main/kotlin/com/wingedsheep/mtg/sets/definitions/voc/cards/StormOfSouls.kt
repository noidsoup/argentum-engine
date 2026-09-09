package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ForEachInCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Storm of Souls — Innistrad: Crimson Vow Commander (VOC) #9
 * {4}{W}{W} · Sorcery
 *
 * Return all creature cards from your graveyard to the battlefield. Each of them is a 1/1 Spirit
 * with flying in addition to its other types. Exile Storm of Souls.
 *
 * Mass reanimation follows the Twilight's Call gather → move pipeline scoped to [Player.You]. The
 * Spirit transformation runs after the move via [ForEachInCollectionEffect] — each returned
 * permanent keeps its other types while gaining Spirit, base 1/1, and flying at
 * [Duration.Permanent] (Supper for Spiders / Molten Sentry composition). The self-exile rider is
 * a second clause in the composite, on [EffectTarget.Self] (Treasured Find).
 */
val StormOfSouls = card("Storm of Souls") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Return all creature cards from your graveyard to the battlefield. Each of them " +
        "is a 1/1 Spirit with flying in addition to its other types. Exile Storm of Souls."

    spell {
        effect = Effects.Composite(
            Effects.Pipeline {
                val graveyardCreatures = gather(
                    CardSource.FromZone(
                        zone = Zone.GRAVEYARD,
                        player = Player.You,
                        filter = GameObjectFilter.Creature,
                    ),
                )
                move(graveyardCreatures, CardDestination.ToZone(Zone.BATTLEFIELD))
                run(
                    ForEachInCollectionEffect(
                        collection = graveyardCreatures.key,
                        effect = Effects.Composite(
                            Effects.SetBasePowerAndToughness(
                                1,
                                1,
                                EffectTarget.Self,
                                Duration.Permanent,
                            ),
                            Effects.AddCreatureType("Spirit", EffectTarget.Self, Duration.Permanent),
                            Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self, Duration.Permanent),
                        ),
                    ),
                )
            },
            Effects.Exile(EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "9"
        artist = "Liiga Smilshkalne"
        flavorText = "Devotion does not end with death."
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30cb1c3f-19d4-47d3-960e-f2de828e6feb.jpg?1783925007"
    }
}
