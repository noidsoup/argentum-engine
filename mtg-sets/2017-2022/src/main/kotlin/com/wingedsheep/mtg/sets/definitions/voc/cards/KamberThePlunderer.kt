package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.EmitLibrarySearchedEventEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Kamber, the Plunderer
 * {3}{B}
 * Legendary Creature — Vampire Rogue
 * 3/4
 *
 * Partner with Laurine, the Diversion (When this creature enters, target player may put Laurine
 * into their hand from their library, then shuffle.)
 * Lifelink
 * Whenever a creature an opponent controls dies, you gain 1 life and create a Blood token.
 */
val KamberThePlunderer = card("Kamber, the Plunderer") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Vampire Rogue"
    oracleText = "Partner with Laurine, the Diversion (When this creature enters, target player may " +
        "put Laurine into their hand from their library, then shuffle.)\n" +
        "Lifelink\n" +
        "Whenever a creature an opponent controls dies, you gain 1 life and create a Blood token. " +
        "(It's an artifact with \"{1}, {T}, Discard a card, Sacrifice this token: Draw a card.\")"
    power = 3
    toughness = 4

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val chosen = target("target player", TargetPlayer())
        effect = MayEffect(
            Effects.Pipeline {
                val searchPool = gather(
                    CardSource.FromZone(
                        zone = Zone.LIBRARY,
                        player = Player.TargetPlayer,
                        filter = GameObjectFilter.Any.named("Laurine, the Diversion"),
                    ),
                    name = "laurineInLibrary",
                )
                val found = chooseUpTo(
                    count = 1,
                    from = searchPool,
                    chooser = Chooser.TargetPlayer,
                    prompt = "Search your library for a card named Laurine, the Diversion",
                    name = "laurineFound",
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
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.opponentControls(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.GainLife(1).then(Effects.CreateBlood(1))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "19"
        artist = "Andrey Kuzinskiy"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2e932f7-1a72-44e7-89f7-b853eada74f0.jpg?1783925004"
    }
}
