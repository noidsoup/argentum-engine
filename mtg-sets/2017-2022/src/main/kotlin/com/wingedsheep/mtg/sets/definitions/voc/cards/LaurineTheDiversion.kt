package com.wingedsheep.mtg.sets.definitions.voc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
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
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Laurine, the Diversion
 * {2}{R}
 * Legendary Creature — Human Rogue
 * 3/3
 *
 * Partner with Kamber, the Plunderer (When this creature enters, target player may put Kamber into
 * their hand from their library, then shuffle.)
 * First strike
 * {2}, Sacrifice an artifact or creature: Goad target creature. (Until your next turn, that
 * creature attacks each combat if able and attacks a player other than you if able.)
 */
val LaurineTheDiversion = card("Laurine, the Diversion") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Rogue"
    oracleText = "Partner with Kamber, the Plunderer (When this creature enters, target player may " +
        "put Kamber into their hand from their library, then shuffle.)\n" +
        "First strike\n" +
        "{2}, Sacrifice an artifact or creature: Goad target creature. (Until your next turn, that " +
        "creature attacks each combat if able and attacks a player other than you if able.)"
    power = 3
    toughness = 3

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val chosen = target("target player", TargetPlayer())
        effect = MayEffect(
            Effects.Pipeline {
                val searchPool = gather(
                    CardSource.FromZone(
                        zone = Zone.LIBRARY,
                        player = Player.TargetPlayer,
                        filter = GameObjectFilter.Any.named("Kamber, the Plunderer"),
                    ),
                    name = "kamberInLibrary",
                )
                val found = chooseUpTo(
                    count = 1,
                    from = searchPool,
                    chooser = Chooser.TargetPlayer,
                    prompt = "Search your library for a card named Kamber, the Plunderer",
                    name = "kamberFound",
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

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Sacrifice(GameObjectFilter.CreatureOrArtifact),
        )
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Goad(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "25"
        artist = "Andrey Kuzinskiy"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/909b3afa-a836-4b8a-9ceb-67559fe42420.jpg?1783924999"
    }
}
