package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect

/**
 * Serpent's Soul-Jar — Kaldheim Commander (KHC) #11
 * {2}{B} · Artifact
 *
 * Whenever an Elf you control dies, exile it.
 * {T}, Pay 2 life: Until end of turn, you may cast a creature spell from among cards exiled
 * with this artifact.
 *
 * The death trigger is Elderfang Venom's Elf-only leaves-the-battlefield shape with a mandatory
 * graveyard-to-exile move linked to this artifact (Illicit Masquerade's `fromZone = GRAVEYARD`
 * wiring). The activated ability gathers the linked-exile pile, narrows it to creature cards,
 * and grants an end-of-turn may-cast permission (Raphael, Most Attitude's attack trigger shape,
 * narrowed to creatures via [FilterCollectionEffect]).
 */
val SerpentsSoulJar = card("Serpent's Soul-Jar") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "Whenever an Elf you control dies, exile it.\n" +
        "{T}, Pay 2 life: Until end of turn, you may cast a creature spell from among cards " +
        "exiled with this artifact."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.withSubtype(Subtype.ELF).youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TriggeringEntity,
                    storeAs = "soulJarDyingElf",
                ),
                MoveCollectionEffect(
                    from = "soulJarDyingElf",
                    destination = CardDestination.ToZone(Zone.EXILE),
                    linkToSource = true,
                ),
            ),
        )
        description = "Whenever an Elf you control dies, exile it."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(2))
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromLinkedExile(),
                    storeAs = "soulJarLinked",
                ),
                FilterCollectionEffect(
                    from = "soulJarLinked",
                    filter = CollectionFilter.MatchesFilter(GameObjectFilter.Creature),
                    storeMatching = "soulJarCreatures",
                ),
                GrantMayPlayFromExileEffect(
                    from = "soulJarCreatures",
                    expiry = MayPlayExpiry.EndOfTurn,
                    nonLandOnly = true,
                ),
            ),
        )
        description = "Until end of turn, you may cast a creature spell from among cards exiled " +
            "with this artifact."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "11"
        artist = "Dan Murayama Scott"
        flavorText = "The coils spiral like time itself, carrying spirits back around from death to reawakening."
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08ae371f-ce62-475f-89a2-1f8a17cf950f.jpg?1783928336"
    }
}
