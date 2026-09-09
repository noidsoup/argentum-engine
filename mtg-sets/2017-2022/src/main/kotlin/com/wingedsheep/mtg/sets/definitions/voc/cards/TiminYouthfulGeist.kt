package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.EmitLibrarySearchedEventEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Timin, Youthful Geist
 * {4}{U}
 * Legendary Creature — Spirit
 * 3/4
 *
 * Partner with Rhoda, Geist Avenger (When this creature enters, target player may put Rhoda into
 * their hand from their library, then shuffle.)
 * Flying
 * At the beginning of each combat, tap up to one target creature.
 */
val TiminYouthfulGeist = card("Timin, Youthful Geist") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Spirit"
    oracleText = "Partner with Rhoda, Geist Avenger (When this creature enters, target player may " +
        "put Rhoda into their hand from their library, then shuffle.)\n" +
        "Flying\n" +
        "At the beginning of each combat, tap up to one target creature."
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val chosen = target("target player", TargetPlayer())
        effect = MayEffect(
            Effects.Pipeline {
                val searchPool = gather(
                    CardSource.FromZone(
                        zone = Zone.LIBRARY,
                        player = Player.TargetPlayer,
                        filter = GameObjectFilter.Any.named("Rhoda, Geist Avenger"),
                    ),
                    name = "rhodaInLibrary",
                )
                val found = chooseUpTo(
                    count = 1,
                    from = searchPool,
                    chooser = Chooser.TargetPlayer,
                    prompt = "Search your library for a card named Rhoda, Geist Avenger",
                    name = "rhodaFound",
                )
                move(
                    from = found,
                    destination = CardDestination.ToZone(Zone.HAND, Player.TargetPlayer),
                    revealed = true,
                )
                run(ShuffleLibraryEffect(chosen))
                run(EmitLibrarySearchedEventEffect)
            },
        )
    }

    triggeredAbility {
        trigger = Triggers.EachCombat
        val creature = target(
            "target",
            TargetCreature(optional = true),
        )
        effect = Effects.Tap(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "16"
        artist = "Randy Vargas"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3d4102e-b48c-4da8-b181-092c7ab1849a.jpg?1783925003"
    }
}
